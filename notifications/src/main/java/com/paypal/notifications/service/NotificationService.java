package com.paypal.notifications.service;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Service that receives incoming notifications and sends them to the proper event sender
 * (users, payments, etc)
 */
public interface NotificationService {

	/**
	 * Process the {@link HyperwalletWebhookNotification} notification and send it to the
	 * proper sender, depending on the strategy
	 * @param incomingNotificationDTO {@link HyperwalletWebhookNotification} notification
	 */
	void processNotification(HyperwalletWebhookNotification incomingNotificationDTO);

	/**
	 * Processes failed notifications that have been stored in the database
	 */
	void processFailedNotifications();

}
