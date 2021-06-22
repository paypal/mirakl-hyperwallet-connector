package com.paypal.sellers.sellersextract.service.strategies;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyFactory;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Strategy Factory class that controls strategies to insert or update business stake
 * holders information for a certain seller into Hyperwallet
 */

@Service
public class HyperWalletBusinessStakeHolderServiceStrategyFactorySingle
		extends SingleAbstractStrategyFactory<BusinessStakeHolderModel, BusinessStakeHolderModel> {

	private final Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> strategies;

	public HyperWalletBusinessStakeHolderServiceStrategyFactorySingle(
			final Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<BusinessStakeHolderModel, BusinessStakeHolderModel>> getStrategies() {
		return strategies;
	}

}
