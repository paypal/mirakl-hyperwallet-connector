package com.paypal.notifications.incoming.services.evaluators;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;

/**
 * Notifications evaluator, checks if a {@link NotificationEntity} is processable.
 */
public interface NotificationEntityEvaluator {

	/**
	 * Checks if a {@link NotificationEntity} is processable.
	 * @param notificationEntity to be checked.
	 * @return true if the {@link NotificationEntity} is processable, false otherwise.
	 */
	boolean isProcessable(final NotificationEntity notificationEntity);

}
