package com.paypal.kyc.consumer;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.service.KYCBusinessStakeholderNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Consumer for the business stakeholder kyc queue
 */
@Service
@Slf4j
public class RabbitMQBusinessStakeHolderKYCConsumer {

	@Resource
	private KYCBusinessStakeholderNotificationService kycBusinessStakeholderNotificationService;

	/**
	 * Just Process and send a business stakeholder kyc notifications received and
	 * processes it to update mirakl
	 * @param notification {@link HyperwalletWebhookNotification}
	 */
	@RabbitListener(queues = "${kyc.business.stakeholders.hyperwallet.queue}",
			errorHandler = "rabbitMQGlobalErrorHandler")
	public void sendKycNotification(final HyperwalletWebhookNotification notification) {
		log.info("Processing HMC incoming Business stakeholder KYC notification [{}] ", notification.getToken());

		kycBusinessStakeholderNotificationService.updateBusinessStakeholderKYCStatus(notification);
	}

}
