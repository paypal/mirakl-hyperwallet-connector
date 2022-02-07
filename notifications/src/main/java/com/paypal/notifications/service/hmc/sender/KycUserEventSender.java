package com.paypal.notifications.service.hmc.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.events.HMCEvent;
import com.paypal.infrastructure.events.KycUserEvent;
import com.paypal.infrastructure.strategy.Strategy;
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

	@Value("${notifications.users.kyc.routingKey}")
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
