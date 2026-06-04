package com.paypal.notifications.incoming.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.notifications.incoming.cache.WebhookNotificationRetriever;
import com.paypal.notifications.incoming.services.converters.NotificationConverter;
import com.paypal.notifications.storage.repositories.NotificationEntityRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.services.NotificationStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of {@link NotificationProcessingQueueService}.
 */
@Slf4j
@Service
public class NotificationProcessingQueueServiceImpl implements NotificationProcessingQueueService {

	private static final String EMAIL_SUBJECT = "[HMC] Technical error occurred when processing the notification %s";

	private static final String EMAIL_BODY = "There was an error processing the notification %s and the operation could not be completed. The maximum number of attempts (%d) has been reached, therefore it will not try to re-process the notification anymore."
			+ "\nPlease check the logs for further information.";

	@Value("${hmc.webhooks.retries.max-retries:5}")
	private int maxRetries;

	/**
	 * Base delay before the first retry attempt. Expressed as an ISO-8601 duration string
	 * (e.g. {@code PT30M} for 30 minutes). Subsequent retries are delayed by
	 * {@code initialRetryDelay * backoffMultiplier^retryCounter}, growing exponentially
	 * with each failure.
	 */
	@Value("${hmc.webhooks.retries.initial-retry-delay:PT1M}")
	private Duration initialRetryDelay;

	/**
	 * Exponential back-off multiplier applied to {@code initialRetryDelay} on each
	 * consecutive failure. A value of {@code 2} doubles the wait time after every failed
	 * attempt (e.g. 30 min → 60 min → 120 min → …).
	 */
	@Value("${hmc.webhooks.retries.backoff-multiplier:2.0}")
	private double backoffMultiplier;

	private final NotificationConverter notificationConverter;

	private final NotificationStorageService notificationStorageService;

	private final NotificationEntityRepository notificationEntityRepository;

	private final WebhookNotificationRetriever webhookNotificationRetriever;

	private final MailNotificationUtil mailNotificationUtil;

	public NotificationProcessingQueueServiceImpl(final NotificationConverter notificationConverter,
			final NotificationStorageService notificationStorageService,
			final NotificationEntityRepository notificationEntityRepository,
			final WebhookNotificationRetriever webhookNotificationRetriever,
			final MailNotificationUtil mailNotificationUtil) {
		this.notificationConverter = notificationConverter;
		this.notificationStorageService = notificationStorageService;
		this.notificationEntityRepository = notificationEntityRepository;
		this.webhookNotificationRetriever = webhookNotificationRetriever;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void enqueue(final HyperwalletWebhookNotification notification) {
		final NotificationEntity entity = notificationConverter.convert(notification);

		if (isDuplicate(entity)) {
			log.warn("Discarding duplicate notification [{}]", entity.getWebHookToken());
			return;
		}

		if (isOutdated(entity)) {
			log.warn("Discarding outdated notification [{}]", entity.getWebHookToken());
			return;
		}

		markSupersededNotificationsAsOutdated(entity);

		entity.setStatus(NotificationStatus.PENDING);
		entity.setRetryCounter(0);
		entity.setProgram(extractProgram(notification));
		notificationStorageService.saveNotification(entity);
		webhookNotificationRetriever.put(notification);
		log.debug("Enqueued notification [{}] with status PENDING", entity.getWebHookToken());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NotificationEntity> fetchNextBatch(final int batchSize) {
		return notificationEntityRepository.findNextBatchForProcessing(new Date(), PageRequest.of(0, batchSize));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void updateStatus(
			final Map<String, NotificationProcessingService.NotificationProcessingStatus> tokenStatusesMap) {
		tokenStatusesMap.forEach((token, status) -> {
			if (NotificationProcessingService.NotificationProcessingStatus.SUCCESS.equals(status)) {
				notificationEntityRepository.updateStatusByWebHookToken(token, NotificationStatus.SUCCESS);
			}
			else {
				resolveFailure(token);
			}
		});
	}

	private void resolveFailure(final String token) {
		final Optional<NotificationEntity> entityOptional = notificationEntityRepository.findByWebHookToken(token);

		if (entityOptional.isEmpty()) {
			log.warn("Cannot resolve failure for unknown notification [{}]", token);
			return;
		}

		final NotificationEntity entity = entityOptional.get();

		final int nextRetryCounter = entity.getRetryCounter() + 1;
		final Date now = new Date();

		if (nextRetryCounter < maxRetries) {
			final Duration delay = computeBackoffDelay(nextRetryCounter);
			final Date nextRetryDate = Date.from(now.toInstant().plus(delay));
			log.warn("Notification [{}] will be retried (attempt {}/{}) — next attempt scheduled after [{}]", token,
					nextRetryCounter, maxRetries, delay);
			notificationEntityRepository.updateStatusAndRetryCounterByWebHookToken(token, NotificationStatus.RETRYING,
					nextRetryCounter, now, nextRetryDate);
		}
		else {
			log.error("Notification [{}] exceeded max retries ({}) — marking as FAILED and sending alert email", token,
					maxRetries);
			notificationEntityRepository.updateStatusAndRetryCounterByWebHookToken(token, NotificationStatus.FAILED,
					nextRetryCounter, now, null);
			mailNotificationUtil.sendPlainTextEmail(EMAIL_SUBJECT.formatted(token),
					EMAIL_BODY.formatted(token, maxRetries));
		}
	}

	/**
	 * Computes the back-off delay for a given retry attempt using the formula:
	 * {@code initialRetryDelay * backoffMultiplier^(retryCounter - 1)}.
	 * <p>
	 * Example with {@code initialRetryDelay=PT30M} and {@code backoffMultiplier=2}:
	 * <ul>
	 * <li>attempt 1 → 30 min</li>
	 * <li>attempt 2 → 60 min</li>
	 * <li>attempt 3 → 120 min</li>
	 * </ul>
	 * @param retryCounter the 1-based attempt number (must be &ge; 1).
	 * @return the computed {@link Duration}.
	 */
	private Duration computeBackoffDelay(final int retryCounter) {
		final double multiplied = initialRetryDelay.toMillis() * Math.pow(backoffMultiplier, (double) retryCounter - 1);
		return Duration.ofMillis((long) multiplied);
	}

	/**
	 * Finds all active ({@code PENDING} or {@code RETRYING}) notifications for the same
	 * {@code objectToken} and {@code notificationType} whose {@code creationDate} is
	 * older than the incoming notification, and marks each of them as
	 * {@link NotificationStatus#OUTDATED} so the job skips them.
	 */
	private void markSupersededNotificationsAsOutdated(final NotificationEntity incomingEntity) {
		final List<NotificationEntity> superseded = notificationEntityRepository.findActiveNotificationsSupersededBy(
				incomingEntity.getObjectToken(), incomingEntity.getNotificationType(),
				incomingEntity.getCreationDate());
		for (final NotificationEntity older : superseded) {
			log.warn("Marking notification [{}] as OUTDATED — superseded by incoming notification for object [{}]",
					older.getWebHookToken(), incomingEntity.getObjectToken());
			notificationEntityRepository.updateStatusByWebHookToken(older.getWebHookToken(),
					NotificationStatus.OUTDATED);
		}
	}

	private boolean isDuplicate(final NotificationEntity entity) {
		return notificationStorageService.getNotificationByWebHookToken(entity.getWebHookToken()).isPresent();
	}

	private boolean isOutdated(final NotificationEntity entity) {
		return notificationStorageService.existsNewerNotificationByObjectToken(entity.getObjectToken(),
				entity.getCreationDate());
	}

	private String extractProgram(final HyperwalletWebhookNotification notification) {
		final Object body = notification.getObject();
		if (body instanceof java.util.Map<?, ?> map) {
			final Object programToken = map.get("programToken");
			if (programToken instanceof String token) {
				return token;
			}
		}
		return null;
	}

}
