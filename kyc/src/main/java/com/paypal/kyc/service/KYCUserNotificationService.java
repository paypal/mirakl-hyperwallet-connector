package com.paypal.kyc.service;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Service to process incoming user kyc notifications
 */
public interface KYCUserNotificationService {

	/**
	 * Based on a {@link HyperwalletWebhookNotification} notification, updates KYC Proof
	 * of Identity/Business status in Mirakl
	 * @param incomingNotificationDTO {@link HyperwalletWebhookNotification} KYC Proof of
	 * Identity/Business status in user notification
	 */
	void updateUserKYCStatus(HyperwalletWebhookNotification incomingNotificationDTO);

	/**
	 * Based on a {@link HyperwalletWebhookNotification} notification, updates document
	 * flags in Mirakl
	 * @param incomingNotificationDTO {@link HyperwalletWebhookNotification} KYC user
	 * notification
	 */
	void updateUserDocumentsFlags(HyperwalletWebhookNotification incomingNotificationDTO);

}
