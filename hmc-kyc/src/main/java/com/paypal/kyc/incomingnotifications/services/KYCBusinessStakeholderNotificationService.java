package com.paypal.kyc.incomingnotifications.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Service to process incoming user kyc notifications
 */
public interface KYCBusinessStakeholderNotificationService {

	/**
	 * Based on a {@link HyperwalletWebhookNotification} notification, updates KYC Proof
	 * of Identity status in Mirakl
	 * @param incomingNotificationDTO {@link HyperwalletWebhookNotification} KYC Proof of
	 * Identity status in user notification
	 */
	void updateBusinessStakeholderKYCStatus(HyperwalletWebhookNotification incomingNotificationDTO);

}
