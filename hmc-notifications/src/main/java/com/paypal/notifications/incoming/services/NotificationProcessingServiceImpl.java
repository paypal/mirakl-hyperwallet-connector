package com.paypal.notifications.incoming.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.incoming.cache.WebhookNotificationRetriever;
import com.paypal.notifications.incoming.handlers.NotificationHandler;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Processes queued {@link NotificationEntity} instances by resolving the full webhook
 * payload from cache and dispatching to the appropriate {@link NotificationHandler}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessingServiceImpl implements NotificationProcessingService {

	private final WebhookNotificationRetriever webhookNotificationRetriever;

	private final List<NotificationHandler> notificationHandlers;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationProcessingStatus processNotification(final NotificationEntity entity) {
		final String token = entity.getWebHookToken();
		log.debug("Processing notification [{}] (attempt {})", token, entity.getRetryCounter() + 1);

		final HyperwalletWebhookNotification notification = webhookNotificationRetriever.get(entity.getProgram(),
				token);
		if (notification == null) {
			log.error("Could not retrieve full notification [{}] — marking as ERROR", token);
			return NotificationProcessingStatus.ERROR;
		}

		return processHyperwalletWebhookNotification(notification, entity);
	}

	// Extracted method to intercept with aspectj
	protected NotificationProcessingStatus processHyperwalletWebhookNotification(
			final HyperwalletWebhookNotification notification, final NotificationEntity entity) {
		final NotificationHandler handler = notificationHandlers.stream()
			.filter(h -> h.supports(entity.getNotificationType()))
			.findFirst()
			.orElse(null);

		if (handler == null) {
			log.warn("No handler found for notification type [{}] — skipping notification [{}]",
					entity.getNotificationType(), entity.getWebHookToken());
			return NotificationProcessingStatus.ERROR;
		}

		try {
			handler.process(entity, notification);
			log.info("Notification [{}] processed successfully", entity.getWebHookToken());
			return NotificationProcessingStatus.SUCCESS;
		}
		catch (final RuntimeException ex) {
			log.error("Notification [{}] could not be processed. Reason: [{}]", entity.getWebHookToken(),
					ex.getClass().getSimpleName() + ": " + ex.getMessage(), ex);
			return NotificationProcessingStatus.ERROR;
		}

	}

}
