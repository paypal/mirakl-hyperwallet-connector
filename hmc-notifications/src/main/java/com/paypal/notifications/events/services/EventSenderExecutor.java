package com.paypal.notifications.events.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.support.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EventSenderExecutor extends SingleAbstractStrategyExecutor<HyperwalletWebhookNotification, Void> {

	private final Set<Strategy<HyperwalletWebhookNotification, Void>> strategies;

	public EventSenderExecutor(final Set<Strategy<HyperwalletWebhookNotification, Void>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<HyperwalletWebhookNotification, Void>> getStrategies() {
		return strategies;
	}

}
