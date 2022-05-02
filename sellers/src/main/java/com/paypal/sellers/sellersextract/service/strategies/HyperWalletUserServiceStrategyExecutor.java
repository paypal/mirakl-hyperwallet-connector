package com.paypal.sellers.sellersextract.service.strategies;

import com.paypal.infrastructure.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.SellerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Executor class that controls strategies to insert or update sellers information into
 * Hyperwallet
 */
@Slf4j
@Service
public class HyperWalletUserServiceStrategyExecutor extends SingleAbstractStrategyExecutor<SellerModel, SellerModel> {

	private final Set<Strategy<SellerModel, SellerModel>> strategies;

	public HyperWalletUserServiceStrategyExecutor(final Set<Strategy<SellerModel, SellerModel>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<SellerModel, SellerModel>> getStrategies() {
		return strategies;
	}

}
