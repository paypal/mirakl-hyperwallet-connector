package com.paypal.invoices.paymentnotifications.handlers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.invoices.paymentnotifications.services.PaymentNotificationService;
import com.paypal.notifications.incoming.handlers.NotificationHandler;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link NotificationHandler} for payment notifications ({@link NotificationType#PMT}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentNotificationHandler implements NotificationHandler {

	private final PaymentNotificationService paymentNotificationService;

	@Override
	public boolean supports(final NotificationType type) {
		return NotificationType.PMT == type;
	}

	@Override
	public void process(final NotificationEntity entity, final HyperwalletWebhookNotification notification) {
		log.info("Processing payment notification [{}]", entity.getWebHookToken());
		paymentNotificationService.processPaymentNotification(notification);
	}

}
