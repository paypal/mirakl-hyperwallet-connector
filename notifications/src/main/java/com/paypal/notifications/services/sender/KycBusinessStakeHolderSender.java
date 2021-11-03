package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.events.HMCEvent;
import com.paypal.infrastructure.events.KycBusinessStakeholderEvent;
import com.paypal.infrastructure.strategy.Strategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Strategy class for sending Kyc business stakeholder notifications
 */

@Slf4j
@Getter
@Service
public class KycBusinessStakeHolderSender extends AbstractHMCEventSender
		implements Strategy<HyperwalletWebhookNotification, Void> {

	@Value("${notifications.business.stakeholders.kyc.routingKey}")
	private String notificationType;

	@Override
	public HMCEvent getEvent(final HyperwalletWebhookNotification notification) {
		return new KycBusinessStakeholderEvent(this, notification);
	}

	@Override
	public boolean isApplicable(final HyperwalletWebhookNotification source) {
		final String type = source.getType();
		return type != null && type.startsWith(getNotificationType());
	}

}
