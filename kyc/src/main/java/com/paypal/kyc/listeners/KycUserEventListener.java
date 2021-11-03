package com.paypal.kyc.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.events.KycUserEvent;
import com.paypal.kyc.service.KYCUserNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Listener for user Kyc events
 */
@Service
@Slf4j
public class KycUserEventListener implements ApplicationListener<KycUserEvent> {

	@Resource
	private KYCUserNotificationService kycNotificationService;

	/**
	 * Receives a user kyc notification and processes it to update Mirakl
	 * @param event {@link KycUserEvent}
	 */
	@Override
	public void onApplicationEvent(final KycUserEvent event) {
		final HyperwalletWebhookNotification notification = event.getNotification();
		log.info("Processing HMC incoming KYC notification [{}] ", notification.getToken());

		kycNotificationService.updateUserKYCStatus(notification);
		kycNotificationService.updateUserDocumentsFlags(notification);
	}

}
