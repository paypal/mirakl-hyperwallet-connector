package com.paypal.notifications.events.services.eventpublishers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.events.model.HMCEvent;
import com.paypal.notifications.events.model.KycUserEvent;
import com.paypal.infrastructure.support.strategy.Strategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Strategy class for sending Kyc user notifications
 */
@Slf4j
@Getter
@Service
public class KycUserEventSender extends AbstractHMCEventSender
		implements Strategy<HyperwalletWebhookNotification, Void> {

	@Value("${hmc.webhooks.routing-keys.kyc-users}")
	private String notificationType;

	@Override
	public HMCEvent getEvent(final HyperwalletWebhookNotification notification) {
		return new KycUserEvent(this, notification);
	}

	@Override
	public boolean isApplicable(final HyperwalletWebhookNotification source) {
		final String type = source.getType();
		return type != null && type.startsWith(getNotificationType());
	}

}
