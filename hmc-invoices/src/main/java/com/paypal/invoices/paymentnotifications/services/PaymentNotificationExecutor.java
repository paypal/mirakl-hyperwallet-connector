package com.paypal.invoices.paymentnotifications.services;

import com.paypal.infrastructure.support.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Payment notification executor
 */
@Service
public class PaymentNotificationExecutor extends SingleAbstractStrategyExecutor<PaymentNotificationBodyModel, Void> {

	private final Set<Strategy<PaymentNotificationBodyModel, Void>> strategies;

	public PaymentNotificationExecutor(final Set<Strategy<PaymentNotificationBodyModel, Void>> strategies) {
		this.strategies = strategies;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Set<Strategy<PaymentNotificationBodyModel, Void>> getStrategies() {
		return strategies;
	}

}
