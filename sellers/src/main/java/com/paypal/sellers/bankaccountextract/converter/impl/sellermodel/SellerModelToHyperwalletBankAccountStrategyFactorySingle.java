package com.paypal.sellers.bankaccountextract.converter.impl.sellermodel;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.strategy.SingleAbstractStrategyFactory;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SellerModelToHyperwalletBankAccountStrategyFactorySingle
		extends SingleAbstractStrategyFactory<SellerModel, HyperwalletBankAccount> {

	private final Set<Strategy<SellerModel, HyperwalletBankAccount>> strategies;

	public SellerModelToHyperwalletBankAccountStrategyFactorySingle(
			final Set<Strategy<SellerModel, HyperwalletBankAccount>> strategies) {
		this.strategies = strategies;
	}

	/**
	 * Returns the set converters from {@link SellerModel} to
	 * {@link HyperwalletBankAccount}
	 * @return the set of converters
	 */
	@Override
	protected Set<Strategy<SellerModel, HyperwalletBankAccount>> getStrategies() {
		return strategies;
	}

}
