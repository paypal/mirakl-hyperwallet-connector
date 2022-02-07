package com.paypal.invoices.paymentnotifications.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.events.PaymentEvent;
import com.paypal.infrastructure.listeners.AbstractNotificationListener;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import com.paypal.invoices.paymentnotifications.service.PaymentNotificationService;
import org.springframework.stereotype.Service;

/**
 * Listener for payment notification events
 */
@Service
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
