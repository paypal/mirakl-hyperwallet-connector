package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MiraklShopToSellerConverterStrategyExecutor
		extends SingleAbstractStrategyExecutor<MiraklShop, SellerModel> {

	private final Set<Strategy<MiraklShop, SellerModel>> strategies;

	public MiraklShopToSellerConverterStrategyExecutor(final Set<Strategy<MiraklShop, SellerModel>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<MiraklShop, SellerModel>> getStrategies() {
		return strategies;
	}

}
