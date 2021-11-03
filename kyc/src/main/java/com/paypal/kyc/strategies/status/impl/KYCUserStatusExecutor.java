package com.paypal.kyc.strategies.status.impl;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class KYCUserStatusExecutor
		extends SingleAbstractStrategyExecutor<KYCUserStatusNotificationBodyModel, Optional<Void>> {

	private final Set<Strategy<KYCUserStatusNotificationBodyModel, Optional<Void>>> strategies;

	public KYCUserStatusExecutor(final Set<Strategy<KYCUserStatusNotificationBodyModel, Optional<Void>>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCUserStatusNotificationBodyModel, Optional<Void>>> getStrategies() {
		return strategies;
	}

}
