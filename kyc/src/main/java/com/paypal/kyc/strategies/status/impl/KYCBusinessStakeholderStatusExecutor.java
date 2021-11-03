package com.paypal.kyc.strategies.status.impl;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class KYCBusinessStakeholderStatusExecutor
		extends SingleAbstractStrategyExecutor<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>> {

	private final Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>>> strategies;

	public KYCBusinessStakeholderStatusExecutor(
			final Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>>> getStrategies() {
		return strategies;
	}

}
