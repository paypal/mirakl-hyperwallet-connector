package com.paypal.sellers.sellerextractioncommons.services.converters;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.support.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MiraklShopToSellerConverterExecutor extends SingleAbstractStrategyExecutor<MiraklShop, SellerModel> {

	private final Set<Strategy<MiraklShop, SellerModel>> strategies;

	public MiraklShopToSellerConverterExecutor(final Set<Strategy<MiraklShop, SellerModel>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<MiraklShop, SellerModel>> getStrategies() {
		return strategies;
	}

}
