package com.paypal.notifications.failures.services;

public interface FailedNotificationService {

	/**
	 * Processes failed notifications that have been stored in the database
	 */
	void processFailedNotifications();

}
