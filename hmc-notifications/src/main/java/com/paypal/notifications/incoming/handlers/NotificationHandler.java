package com.paypal.notifications.incoming.handlers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationType;

/**
 * Strategy interface for processing a specific type of Hyperwallet webhook notification.
 * Implementations are discovered by the {@code NotificationProcessJob} and invoked for
 * each notification whose type is supported.
 */
public interface NotificationHandler {

	/**
	 * Returns {@code true} if this handler is able to process notifications of the given
	 * type.
	 * @param type the {@link NotificationType} of the incoming notification.
	 * @return {@code true} if supported, {@code false} otherwise.
	 */
	boolean supports(NotificationType type);

	/**
	 * Processes the notification.
	 * @param entity the {@link NotificationEntity} stored in the queue.
	 * @param notification the full {@link HyperwalletWebhookNotification} (from cache or
	 * SDK).
	 */
	void process(NotificationEntity entity, HyperwalletWebhookNotification notification);

}
