package com.paypal.kyc.incomingnotifications.handlers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.incomingnotifications.services.KYCUserNotificationService;
import com.paypal.notifications.incoming.handlers.NotificationHandler;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link NotificationHandler} for KYC user notifications ({@link NotificationType#USR}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KycUserNotificationHandler implements NotificationHandler {

	private final KYCUserNotificationService kycUserNotificationService;

	@Override
	public boolean supports(final NotificationType type) {
		return NotificationType.USR == type;
	}

	@Override
	public void process(final NotificationEntity entity, final HyperwalletWebhookNotification notification) {
		log.info("Processing KYC user notification [{}]", entity.getWebHookToken());
		kycUserNotificationService.updateUserKYCStatus(notification);
		kycUserNotificationService.updateUserDocumentsFlags(notification);
	}

}
