package com.paypal.kyc.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.events.KycBusinessStakeholderEvent;
import com.paypal.infrastructure.listeners.AbstractNotificationListener;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import com.paypal.kyc.service.KYCBusinessStakeholderNotificationService;
import org.springframework.stereotype.Service;

/**
 * Listener for business stakeholder Kyc events
 */
@Service
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
