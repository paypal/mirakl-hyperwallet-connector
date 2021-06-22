package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.strategy.SingleAbstractStrategyFactory;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MiraklShopToSellerConverterStrategyFactorySingle
		extends SingleAbstractStrategyFactory<MiraklShop, SellerModel> {

	private final Set<Strategy<MiraklShop, SellerModel>> strategies;

	public MiraklShopToSellerConverterStrategyFactorySingle(final Set<Strategy<MiraklShop, SellerModel>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<MiraklShop, SellerModel>> getStrategies() {
		return strategies;
	}

}
