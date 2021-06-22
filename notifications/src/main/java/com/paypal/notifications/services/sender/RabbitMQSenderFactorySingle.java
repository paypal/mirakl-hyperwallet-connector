package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.SingleAbstractStrategyFactory;
import com.paypal.infrastructure.strategy.Strategy;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class RabbitMQSenderFactorySingle
		extends SingleAbstractStrategyFactory<HyperwalletWebhookNotification, Optional<Void>> {

	private final Set<Strategy<HyperwalletWebhookNotification, Optional<Void>>> strategies;

	public RabbitMQSenderFactorySingle(final Set<Strategy<HyperwalletWebhookNotification, Optional<Void>>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<HyperwalletWebhookNotification, Optional<Void>>> getStrategies() {
		return strategies;
	}

}
