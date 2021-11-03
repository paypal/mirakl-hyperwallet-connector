package com.paypal.invoices.paymentnotifications.service;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Payment notification strategy factory
 */
@Service
public class PaymentNotificationExecutor
		extends SingleAbstractStrategyExecutor<PaymentNotificationBodyModel, Optional<Void>> {

	private final Set<Strategy<PaymentNotificationBodyModel, Optional<Void>>> strategies;

	public PaymentNotificationExecutor(final Set<Strategy<PaymentNotificationBodyModel, Optional<Void>>> strategies) {
		this.strategies = strategies;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Set<Strategy<PaymentNotificationBodyModel, Optional<Void>>> getStrategies() {
		return strategies;
	}

}
