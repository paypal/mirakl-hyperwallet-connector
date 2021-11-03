package com.paypal.kyc.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.events.KycBusinessStakeholderEvent;
import com.paypal.kyc.service.KYCBusinessStakeholderNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Listener for business stakeholder Kyc events
 */
@Service
@Slf4j
public class KycBusinessStakeHolderKYCListener implements ApplicationListener<KycBusinessStakeholderEvent> {

	@Resource
	private KYCBusinessStakeholderNotificationService kycBusinessStakeholderNotificationService;

	/**
	 * Receives a business stakeholder kyc notification and processes it to update Mirakl
	 * @param event {@link KycBusinessStakeholderEvent}
	 */
	@Override
	public void onApplicationEvent(final KycBusinessStakeholderEvent event) {
		final HyperwalletWebhookNotification notification = event.getNotification();
		log.info("Processing HMC incoming Business stakeholder KYC notification [{}] ", notification.getToken());

		kycBusinessStakeholderNotificationService.updateBusinessStakeholderKYCStatus(notification);
	}

}
