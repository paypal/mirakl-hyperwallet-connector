package com.paypal.notifications.failures.services;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.failures.connectors.NotificationsRepository;
import com.paypal.notifications.failures.repositories.FailedNotificationInformationRepository;
import com.paypal.notifications.incoming.services.NotificationProcessingService;
import com.paypal.notifications.storage.repositories.entities.NotificationInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FailedNotificationServiceImpl implements FailedNotificationService {

	private final NotificationsRepository notificationsRepository;
	private final FailedNotificationInformationRepository failedNotificationInformationRepository;
	private final NotificationProcessingService notificationProcessingService;
	private final FailedNotificationRetryMarker retryMarker;

	@Value("${hmc.webhooks.retries.max-retries:5}")
	private int maxRetries;

	public FailedNotificationServiceImpl(final NotificationsRepository notificationsRepository,
										 final FailedNotificationInformationRepository failedNotificationInformationRepository,
										 final NotificationProcessingService notificationProcessingService,
										 final FailedNotificationRetryMarker retryMarker) {
		this.notificationsRepository = notificationsRepository;
		this.failedNotificationInformationRepository = failedNotificationInformationRepository;
		this.notificationProcessingService = notificationProcessingService;
		this.retryMarker = retryMarker;
	}

	@Override
	public void processFailedNotifications() {
		final Iterable<NotificationInfoEntity> failedNotifications = failedNotificationInformationRepository.findAll();

		for (NotificationInfoEntity entity : failedNotifications) {
			final String token = entity.getNotificationToken();
			try {
				log.info("Reprocessing notification [{}], retryCounter={}", token, entity.getRetryCounter());

				Integer current = entity.getRetryCounter();
				int currentRetries = current == null ? 0 : current;
				if (currentRetries >= maxRetries) {
					log.warn("Notification [{}] exceeded max retries ({}). Expiring.", token, maxRetries);
					retryMarker.expireAndRemove(token);
					continue;
				}

				HyperwalletWebhookNotification notification = notificationsRepository.getHyperwalletWebhookNotification(
						entity.getProgram(),
						token
				);

				notificationProcessingService.processNotification(notification);

				// success -> delete so it doesn't come back in retry job
				retryMarker.markSucceededAndRemove(token);

			}
			catch (Exception e) {
				// fail -> always increment
				retryMarker.incrementFailures(token);

				// logging
				if (e instanceof HyperwalletException hw) {
					log.warn("Retry failed for token={} due to HyperwalletException: {}", token, hw.getMessage());
				}
				else {
					log.warn("Retry failed for token={} due to {}", token, e.toString());
				}
			}
		}
	}

}
