package com.paypal.notifications.evaluator;

import com.paypal.notifications.model.entity.NotificationEntity;

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
