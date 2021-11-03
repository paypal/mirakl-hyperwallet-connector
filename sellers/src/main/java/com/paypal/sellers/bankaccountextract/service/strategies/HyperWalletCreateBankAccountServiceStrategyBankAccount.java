package com.paypal.sellers.bankaccountextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.sellers.bankaccountextract.service.MiraklBankAccountExtractService;
import com.paypal.sellers.entity.FailedBankAccountInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.FailedEntityInformationService;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Strategy class that creates a new bank account into hyperwallet and updates the token
 * in mirakl
 */
@Service
public class HyperWalletCreateBankAccountServiceStrategyBankAccount
		extends AbstractHyperwalletBankAccountRetryApiStrategy {

	private final MiraklBankAccountExtractService miraklBankAccountExtractService;

	protected HyperWalletCreateBankAccountServiceStrategyBankAccount(
			final FailedEntityInformationService<FailedBankAccountInformation> failedEntityInformationService,
			final StrategyExecutor<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyExecutor,
			final MiraklBankAccountExtractService miraklBankAccountExtractService,
			final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil) {
		super(failedEntityInformationService, sellerModelToHyperwalletBankAccountStrategyExecutor,
				hyperwalletSDKService, mailNotificationUtil);
		this.miraklBankAccountExtractService = miraklBankAccountExtractService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<HyperwalletBankAccount> execute(final SellerModel seller) {
		final var hwBankAccountCreated = callSuperExecute(seller);
		hwBankAccountCreated
				.ifPresent(bankAccount -> miraklBankAccountExtractService.updateBankAccountToken(seller, bankAccount));
		return hwBankAccountCreated;
	}

	@Override
	protected HyperwalletBankAccount callMiraklAPI(final String hyperwalletProgram,
			final HyperwalletBankAccount hyperwalletBankAccount) {
		final Hyperwallet hyperwallet = hyperwalletSDKService
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
