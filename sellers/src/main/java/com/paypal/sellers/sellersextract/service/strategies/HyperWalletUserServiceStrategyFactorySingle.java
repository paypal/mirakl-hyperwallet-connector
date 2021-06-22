package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.strategy.SingleAbstractStrategyFactory;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.SellerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Strategy Factory class that controls strategies to insert or update sellers information
 * into Hyperwallet
 */
@Slf4j
@Service
public class HyperWalletUserServiceStrategyFactorySingle
		extends SingleAbstractStrategyFactory<SellerModel, HyperwalletUser> {

	private final Set<Strategy<SellerModel, HyperwalletUser>> strategies;

	public HyperWalletUserServiceStrategyFactorySingle(final Set<Strategy<SellerModel, HyperwalletUser>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<SellerModel, HyperwalletUser>> getStrategies() {
		return strategies;
	}

}
