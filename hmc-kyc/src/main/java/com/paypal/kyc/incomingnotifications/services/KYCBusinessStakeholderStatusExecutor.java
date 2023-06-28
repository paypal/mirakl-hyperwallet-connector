package com.paypal.kyc.incomingnotifications.services;

import com.paypal.infrastructure.support.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.incomingnotifications.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class KYCBusinessStakeholderStatusExecutor
		extends SingleAbstractStrategyExecutor<KYCBusinessStakeholderStatusNotificationBodyModel, Void> {

	private final Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Void>> strategies;

	public KYCBusinessStakeholderStatusExecutor(
			final Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Void>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Void>> getStrategies() {
		return strategies;
	}

}
