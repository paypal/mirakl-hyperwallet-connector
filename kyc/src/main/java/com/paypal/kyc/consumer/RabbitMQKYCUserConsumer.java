package com.paypal.kyc.consumer;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.service.KYCUserNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Consumer for the user kyc queue
 */
@Service
@Slf4j
public class RabbitMQKYCUserConsumer {

	@Resource
	private KYCUserNotificationService kycNotificationService;

	/**
	 * Just Process and send a user kyc notifications received and processes it to update
	 * mirakl
	 * @param notification {@link HyperwalletWebhookNotification}
	 */
	@RabbitListener(queues = "${kyc.users.hyperwallet.queue}", errorHandler = "rabbitMQGlobalErrorHandler")
	public void sendKycNotification(final HyperwalletWebhookNotification notification) {
		log.info("Processing HMC incoming KYC notification [{}] ", notification.getToken());

		kycNotificationService.updateUserKYCStatus(notification);
		kycNotificationService.updateUserDocumentsFlags(notification);
	}

}
