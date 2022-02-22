package com.paypal.kyc.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.events.KycUserEvent;
import com.paypal.infrastructure.listeners.AbstractNotificationListener;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import com.paypal.kyc.service.KYCUserNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Listener for user Kyc events
 */
@Service
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
