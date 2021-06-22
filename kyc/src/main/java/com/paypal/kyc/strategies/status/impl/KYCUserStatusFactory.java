package com.paypal.kyc.strategies.status.impl;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyFactory;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class KYCUserStatusFactory
		extends SingleAbstractStrategyFactory<KYCUserStatusNotificationBodyModel, Optional<Void>> {

	private final Set<Strategy<KYCUserStatusNotificationBodyModel, Optional<Void>>> strategies;

	public KYCUserStatusFactory(final Set<Strategy<KYCUserStatusNotificationBodyModel, Optional<Void>>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCUserStatusNotificationBodyModel, Optional<Void>>> getStrategies() {
		return strategies;
	}

}
