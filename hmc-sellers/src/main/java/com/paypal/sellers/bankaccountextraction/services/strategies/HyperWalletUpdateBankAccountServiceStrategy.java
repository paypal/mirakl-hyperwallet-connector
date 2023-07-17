package com.paypal.sellers.bankaccountextraction.services.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Strategy class that updates an existing bank account into hyperwallet
 */
@Service
public class HyperWalletUpdateBankAccountServiceStrategy extends AbstractHyperwalletBankAccountStrategy {

	protected HyperWalletUpdateBankAccountServiceStrategy(
			final StrategyExecutor<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyExecutor,
			final UserHyperwalletSDKService userHyperwalletSDKService,
			final MailNotificationUtil mailNotificationUtil) {
		super(sellerModelToHyperwalletBankAccountStrategyExecutor, userHyperwalletSDKService, mailNotificationUtil);
	}

	/**
	 * Checks whether the strategy must be executed based on the {@code source}
	 * @param seller the source object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final SellerModel seller) {
		return Objects.nonNull(seller.getBankAccountDetails().getToken());
	}

	@Override
	protected HyperwalletBankAccount callHyperwalletAPI(final String hyperwalletProgram,
			final HyperwalletBankAccount hyperwalletBankAccount) {
		final Hyperwallet hyperwallet = userHyperwalletSDKService
				.getHyperwalletInstanceByHyperwalletProgram(hyperwalletProgram);
		return hyperwallet.updateBankAccount(hyperwalletBankAccount);
	}

}
