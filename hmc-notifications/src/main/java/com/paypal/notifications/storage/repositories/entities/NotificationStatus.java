package com.paypal.notifications.storage.repositories.entities;

/**
 * Processing status of a {@link NotificationEntity} in the notification queue.
 */
public enum NotificationStatus {

	/**
	 * Notification has been received and is waiting to be processed.
	 */
	PENDING,

	/**
	 * Notification processing failed and will be retried.
	 */
	RETRYING,

	/**
	 * Notification processing failed and has exhausted all retries.
	 */
	FAILED,

	/**
	 * Notification has been processed successfully.
	 */
	SUCCESS,

	/**
	 * Notification has been superseded by a newer notification for the same object and
	 * type that arrived while this one was still pending or retrying. It will not be
	 * processed.
	 */
	OUTDATED

}
