package com.paypal.kyc.incomingnotifications.handlers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.incomingnotifications.services.KYCBusinessStakeholderNotificationService;
import com.paypal.notifications.incoming.handlers.NotificationHandler;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link NotificationHandler} for KYC business stakeholder notifications
 * ({@link NotificationType#STK}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KycBusinessStakeholderNotificationHandler implements NotificationHandler {

	private final KYCBusinessStakeholderNotificationService kycBusinessStakeholderNotificationService;

	@Override
	public boolean supports(final NotificationType type) {
		return NotificationType.STK == type;
	}

	@Override
	public void process(final NotificationEntity entity, final HyperwalletWebhookNotification notification) {
		log.info("Processing KYC business stakeholder notification [{}]", entity.getWebHookToken());
		kycBusinessStakeholderNotificationService.updateBusinessStakeholderKYCStatus(notification);
	}

}
