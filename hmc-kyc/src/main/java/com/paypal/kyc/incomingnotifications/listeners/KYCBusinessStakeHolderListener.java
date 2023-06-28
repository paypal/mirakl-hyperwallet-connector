package com.paypal.kyc.incomingnotifications.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.incomingnotifications.services.KYCBusinessStakeholderNotificationService;
import com.paypal.notifications.events.model.KycBusinessStakeholderEvent;
import com.paypal.notifications.events.support.AbstractNotificationListener;
import com.paypal.notifications.failures.repositories.FailedNotificationInformationRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationInfoEntity;
import org.springframework.stereotype.Component;

/**
 * Listener for business stakeholder Kyc events
 */
@Component
public class KYCBusinessStakeHolderListener extends AbstractNotificationListener<KycBusinessStakeholderEvent> {

	private static final String NOTIFICATION_TYPE = "Business stakeholder KYC";

	private final KYCBusinessStakeholderNotificationService kycBusinessStakeholderNotificationService;

	public KYCBusinessStakeHolderListener(final MailNotificationUtil mailNotificationUtil,
			final FailedNotificationInformationRepository failedNotificationInformationRepository,
			final KYCBusinessStakeholderNotificationService kycBusinessStakeholderNotificationService,
			final Converter<HyperwalletWebhookNotification, NotificationInfoEntity> notificationInfoEntityToNotificationConverter) {
		super(mailNotificationUtil, failedNotificationInformationRepository,
				notificationInfoEntityToNotificationConverter);
		this.kycBusinessStakeholderNotificationService = kycBusinessStakeholderNotificationService;
	}

	@Override
	protected void processNotification(final HyperwalletWebhookNotification notification) {
		kycBusinessStakeholderNotificationService.updateBusinessStakeholderKYCStatus(notification);
	}

	@Override
	protected String getNotificationType() {
		return NOTIFICATION_TYPE;
	}

}
