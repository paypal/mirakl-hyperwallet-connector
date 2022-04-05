package com.paypal.sellers.sellersextract.service.strategies;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Executor class that controls strategies to insert or update business stake holders
 * information for a certain seller into Hyperwallet
 */
@Service
public class HyperWalletBusinessStakeHolderServiceExecutor
		extends SingleAbstractStrategyExecutor<BusinessStakeHolderModel, BusinessStakeHolderModel> {

	private final Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> strategies;

	public HyperWalletBusinessStakeHolderServiceExecutor(
			final Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> getStrategies() {
		return strategies;
	}

}
