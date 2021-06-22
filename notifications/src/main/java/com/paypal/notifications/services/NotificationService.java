package com.paypal.notifications.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Service that receives incoming notifications and send them to the proper queue (users,
 * payments, etc)
 */
public interface NotificationService {

	/**
	 * Process the {@link HyperwalletWebhookNotification} notification and send it to the
	 * proper queue, depending on the strategy
	 * @param incomingNotificationDTO {@link HyperwalletWebhookNotification} Payment
	 * notification
	 */
	void processNotification(HyperwalletWebhookNotification incomingNotificationDTO);

}
