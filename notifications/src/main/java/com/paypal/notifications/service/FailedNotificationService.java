package com.paypal.notifications.service;

public interface FailedNotificationService {

	/**
	 * Processes failed notifications that have been stored in the database
	 */
	void processFailedNotifications();

}
