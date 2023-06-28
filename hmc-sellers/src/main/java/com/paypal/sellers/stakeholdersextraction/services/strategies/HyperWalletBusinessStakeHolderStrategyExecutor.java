package com.paypal.sellers.stakeholdersextraction.services.strategies;

import com.paypal.infrastructure.support.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Executor class that controls strategies to insert or update business stake holders
 * information for a certain seller into Hyperwallet
 */
@Service
public class HyperWalletBusinessStakeHolderStrategyExecutor
		extends SingleAbstractStrategyExecutor<BusinessStakeHolderModel, BusinessStakeHolderModel> {

	private final Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> strategies;

	public HyperWalletBusinessStakeHolderStrategyExecutor(
			final Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> getStrategies() {
		return strategies;
	}

}
