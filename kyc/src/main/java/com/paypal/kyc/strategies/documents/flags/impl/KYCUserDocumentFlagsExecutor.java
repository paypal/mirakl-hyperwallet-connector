package com.paypal.kyc.strategies.documents.flags.impl;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class KYCUserDocumentFlagsExecutor
		extends SingleAbstractStrategyExecutor<KYCUserDocumentFlagsNotificationBodyModel, Optional<Void>> {

	private final Set<Strategy<KYCUserDocumentFlagsNotificationBodyModel, Optional<Void>>> strategies;

	public KYCUserDocumentFlagsExecutor(
			final Set<Strategy<KYCUserDocumentFlagsNotificationBodyModel, Optional<Void>>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCUserDocumentFlagsNotificationBodyModel, Optional<Void>>> getStrategies() {
		return strategies;
	}

}
