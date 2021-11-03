package com.paypal.invoices.paymentnotifications.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.events.PaymentEvent;
import com.paypal.invoices.paymentnotifications.service.PaymentNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Listener for payment notification events
 */
@Service
@Slf4j
public class PaymentListener implements ApplicationListener<PaymentEvent> {

	@Resource
	private PaymentNotificationService paymentNotificationService;

	/**
	 * Receives a payment notification and processes it to update Mirakl
	 * @param event {@link PaymentEvent}
	 */
	@Override
	public void onApplicationEvent(final PaymentEvent event) {
		final HyperwalletWebhookNotification notification = event.getNotification();
		log.info("Processing HMC incoming payment notification [{}] ", notification.getToken());

		paymentNotificationService.processPaymentNotification(notification);
	}

}
