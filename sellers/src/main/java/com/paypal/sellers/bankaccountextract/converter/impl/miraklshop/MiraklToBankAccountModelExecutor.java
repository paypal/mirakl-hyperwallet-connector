package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.strategy.SingleAbstractStrategyExecutor;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class MiraklToBankAccountModelExecutor extends SingleAbstractStrategyExecutor<MiraklShop, BankAccountModel> {

	private final Set<Strategy<MiraklShop, BankAccountModel>> strategies;

	public MiraklToBankAccountModelExecutor(final Set<Strategy<MiraklShop, BankAccountModel>> strategies) {
		this.strategies = strategies;
	}

	/**
	 * Returns the set converters from {@link MiraklShop} to {@link BankAccountModel}
	 * @return the set of converters
	 */
	@Override
	protected Set<Strategy<MiraklShop, BankAccountModel>> getStrategies() {
		return strategies;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankAccountModel execute(final MiraklShop source) {
		if (Objects.isNull(source.getPaymentInformation())) {
			log.warn("No bank account info for shop code: [{}]", source.getId());
			return null;
		}
		return callSuperExecute(source);
	}

	protected BankAccountModel callSuperExecute(final MiraklShop source) {
		return super.execute(source);
	}

}
