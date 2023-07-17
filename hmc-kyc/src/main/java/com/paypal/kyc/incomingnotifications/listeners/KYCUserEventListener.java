package com.paypal.kyc.incomingnotifications.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.incomingnotifications.services.KYCUserNotificationService;
import com.paypal.notifications.events.model.KycUserEvent;
import com.paypal.notifications.events.support.AbstractNotificationListener;
import com.paypal.notifications.failures.repositories.FailedNotificationInformationRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Listener for user Kyc events
 */
@Component
@Slf4j
public class KYCUserEventListener extends AbstractNotificationListener<KycUserEvent> {

	private static final String NOTIFICATION_TYPE = "KYC";

	private final KYCUserNotificationService kycNotificationService;

	public KYCUserEventListener(final MailNotificationUtil mailNotificationUtil,
			final KYCUserNotificationService kycNotificationService,
			final FailedNotificationInformationRepository failedNotificationInformationRepository,
			final Converter<HyperwalletWebhookNotification, NotificationInfoEntity> notificationInfoEntityToNotificationConverter) {
		super(mailNotificationUtil, failedNotificationInformationRepository,
				notificationInfoEntityToNotificationConverter);
		this.kycNotificationService = kycNotificationService;
	}

	@Override
	protected void processNotification(final HyperwalletWebhookNotification notification) {
		kycNotificationService.updateUserKYCStatus(notification);
		kycNotificationService.updateUserDocumentsFlags(notification);
	}

	@Override
	protected String getNotificationType() {
		return NOTIFICATION_TYPE;
	}

}
