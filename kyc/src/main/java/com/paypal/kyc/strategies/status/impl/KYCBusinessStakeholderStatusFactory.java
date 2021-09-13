package com.paypal.kyc.strategies.status.impl;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyFactory;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class KYCBusinessStakeholderStatusFactory
		extends SingleAbstractStrategyFactory<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>> {

	private final Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>>> strategies;

	public KYCBusinessStakeholderStatusFactory(
			final Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>>> getStrategies() {
		return strategies;
	}

}
