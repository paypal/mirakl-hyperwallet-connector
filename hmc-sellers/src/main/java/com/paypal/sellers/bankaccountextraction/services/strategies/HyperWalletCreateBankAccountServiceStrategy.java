package com.paypal.sellers.bankaccountextraction.services.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.sellers.bankaccountextraction.services.MiraklBankAccountExtractService;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Strategy class that creates a new bank account into hyperwallet and updates the token
 * in mirakl
 */
@Service
public class HyperWalletCreateBankAccountServiceStrategy extends AbstractHyperwalletBankAccountStrategy {

	private final MiraklBankAccountExtractService miraklBankAccountExtractService;

	protected HyperWalletCreateBankAccountServiceStrategy(
			final StrategyExecutor<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyExecutor,
			final MiraklBankAccountExtractService miraklBankAccountExtractService,
			final UserHyperwalletSDKService userHyperwalletSDKService,
			final MailNotificationUtil mailNotificationUtil) {
		super(sellerModelToHyperwalletBankAccountStrategyExecutor, userHyperwalletSDKService, mailNotificationUtil);
		this.miraklBankAccountExtractService = miraklBankAccountExtractService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<HyperwalletBankAccount> execute(final SellerModel seller) {
		final Optional<HyperwalletBankAccount> hwBankAccountCreated = callSuperExecute(seller);
		hwBankAccountCreated
				.ifPresent(bankAccount -> miraklBankAccountExtractService.updateBankAccountToken(seller, bankAccount));
		return hwBankAccountCreated;
	}

	@Override
	protected HyperwalletBankAccount callHyperwalletAPI(final String hyperwalletProgram,
			final HyperwalletBankAccount hyperwalletBankAccount) {
		final Hyperwallet hyperwallet = userHyperwalletSDKService
				.getHyperwalletInstanceByHyperwalletProgram(hyperwalletProgram);
		return hyperwallet.createBankAccount(hyperwalletBankAccount);
	}

	/**
	 * Checks whether the strategy must be executed based on the {@code source}
	 * @param seller the source object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final SellerModel seller) {
		return Objects.isNull(seller.getBankAccountDetails().getToken());
	}

	protected Optional<HyperwalletBankAccount> callSuperExecute(final SellerModel seller) {
		return super.execute(seller);
	}

}
