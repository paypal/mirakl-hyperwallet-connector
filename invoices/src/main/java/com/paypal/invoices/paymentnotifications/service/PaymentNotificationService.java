package com.paypal.invoices.paymentnotifications.service;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Service to process incoming payment notifications
 */
public interface PaymentNotificationService {

	/**
	 * Process the {@link HyperwalletWebhookNotification} notification and send it to
	 * Mirakl
	 * @param incomingNotificationDTO {@link HyperwalletWebhookNotification} Payment
	 * notification
	 */
	void processPaymentNotification(HyperwalletWebhookNotification incomingNotificationDTO);

}
