package com.paypal.invoices.paymentnotifications.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.paymentnotifications.services.PaymentNotificationService;
import com.paypal.notifications.events.model.PaymentEvent;
import com.paypal.notifications.events.support.AbstractNotificationListener;
import com.paypal.notifications.failures.repositories.FailedNotificationInformationRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationInfoEntity;
import org.springframework.stereotype.Component;

/**
 * Listener for payment notification events
 */
@Component
public class PaymentListener extends AbstractNotificationListener<PaymentEvent> {

	private static final String NOTIFICATION_TYPE = "payment";

	private final PaymentNotificationService paymentNotificationService;

	public PaymentListener(final MailNotificationUtil mailNotificationUtil,
			final PaymentNotificationService paymentNotificationService,
			final FailedNotificationInformationRepository failedNotificationInformationRepository,
			final Converter<HyperwalletWebhookNotification, NotificationInfoEntity> notificationInfoEntityToNotificationConverter) {
		super(mailNotificationUtil, failedNotificationInformationRepository,
				notificationInfoEntityToNotificationConverter);
		this.paymentNotificationService = paymentNotificationService;
	}

	@Override
	protected void processNotification(final HyperwalletWebhookNotification notification) {
		paymentNotificationService.processPaymentNotification(notification);
	}

	@Override
	protected String getNotificationType() {
		return NOTIFICATION_TYPE;
	}

}
