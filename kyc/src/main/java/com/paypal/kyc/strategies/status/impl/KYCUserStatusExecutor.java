package com.paypal.kyc.strategies.status.impl;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class KYCUserStatusExecutor extends SingleAbstractStrategyExecutor<KYCUserStatusNotificationBodyModel, Void> {

	private final Set<Strategy<KYCUserStatusNotificationBodyModel, Void>> strategies;

	public KYCUserStatusExecutor(final Set<Strategy<KYCUserStatusNotificationBodyModel, Void>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCUserStatusNotificationBodyModel, Void>> getStrategies() {
		return strategies;
	}

}
