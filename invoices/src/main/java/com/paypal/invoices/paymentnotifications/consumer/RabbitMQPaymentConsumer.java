package com.paypal.invoices.paymentnotifications.consumer;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.invoices.paymentnotifications.service.PaymentNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Consumer for the payment notification queue
 */
@Service
@Slf4j
public class RabbitMQPaymentConsumer {

	@Resource
	private PaymentNotificationService paymentNotificationService;

	/**
	 * Just Process and send a payment notifications received and processes it to update
	 * mirakl
	 * @param notification {@link HyperwalletWebhookNotification}
	 */
	@RabbitListener(queues = "${invoices.payments.queue}", errorHandler = "rabbitMQGlobalErrorHandler")
	public void sendPaymentNotification(final HyperwalletWebhookNotification notification) {
		log.info("Processing HMC incoming notification [{}] ", notification.getToken());

		paymentNotificationService.processPaymentNotification(notification);
	}

}
