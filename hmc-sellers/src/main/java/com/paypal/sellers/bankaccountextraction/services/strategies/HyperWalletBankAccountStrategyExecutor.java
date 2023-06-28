package com.paypal.sellers.bankaccountextraction.services.strategies;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.support.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Executor class that controls strategies to insert or update bank account information
 * into Hyperwallet
 */
@Slf4j
@Service
public class HyperWalletBankAccountStrategyExecutor
		extends SingleAbstractStrategyExecutor<SellerModel, Optional<HyperwalletBankAccount>> {

	private final Set<Strategy<SellerModel, Optional<HyperwalletBankAccount>>> strategies;

	public HyperWalletBankAccountStrategyExecutor(
			final Set<Strategy<SellerModel, Optional<HyperwalletBankAccount>>> strategies) {
		this.strategies = strategies;
	}

	@Override
	protected Set<Strategy<SellerModel, Optional<HyperwalletBankAccount>>> getStrategies() {
		return strategies;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<HyperwalletBankAccount> execute(final SellerModel seller) {
		if (Objects.isNull(seller.getBankAccountDetails())) {
			log.warn("No bank account info for shop code: [{}]", seller.getClientUserId());
			return Optional.empty();
		}
		return callSuperExecute(seller);
	}

	protected Optional<HyperwalletBankAccount> callSuperExecute(final SellerModel seller) {
		return super.execute(seller);
	}

}
