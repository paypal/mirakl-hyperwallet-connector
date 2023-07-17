package com.paypal.kyc.incomingnotifications.services;

import com.paypal.infrastructure.support.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class KYCUserDocumentFlagsExecutor
		extends SingleAbstractStrategyExecutor<KYCUserDocumentFlagsNotificationBodyModel, Void> {

	private final Set<Strategy<KYCUserDocumentFlagsNotificationBodyModel, Void>> strategies;

	public KYCUserDocumentFlagsExecutor(
			final Set<Strategy<KYCUserDocumentFlagsNotificationBodyModel, Void>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<KYCUserDocumentFlagsNotificationBodyModel, Void>> getStrategies() {
		return strategies;
	}

}
