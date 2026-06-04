package com.paypal.notifications.storage.services;

/**
 * Service responsible for purging old {@code NotificationEntity} records that have
 * reached a terminal processing status.
 */
public interface NotificationCleanupService {

	/**
	 * Deletes all notifications whose {@code receptionDate} is older than the configured
	 * retention period <strong>and</strong> whose status is one of the terminal values
	 * eligible for cleanup ({@code FAILED}, {@code SUCCESS}, {@code OUTDATED}).
	 */
	void deleteExpiredNotifications();

}
