package com.paypal.sellers.bankaccountextract.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.mirakl.client.core.exception.MiraklApiException;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.service.TokenSynchronizationService;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.bankaccountextract.service.MiraklBankAccountExtractService;
import com.paypal.sellers.sellersextract.model.SellerModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Class that implements the {@link TokenSynchronizationService} interface for the
 * synchronization of tokens for bank accounts
 */
@Slf4j
@Service("bankAccountTokenSynchronizationService")
public class BankAccountTokenSynchronizationServiceImpl implements TokenSynchronizationService<SellerModel> {

	private final HyperwalletSDKUserService hyperwalletSDKUserService;

	private final MiraklBankAccountExtractService miraklBankAccountExtractService;

	private final HyperwalletMiraklBankAccountMatcher hyperwalletMiraklBankAccountMatcher;

	public BankAccountTokenSynchronizationServiceImpl(final HyperwalletSDKUserService hyperwalletSDKUserService,
			final MiraklBankAccountExtractService miraklBankAccountExtractService,
			HyperwalletMiraklBankAccountMatcher hyperwalletMiraklBankAccountMatcher) {

		this.hyperwalletSDKUserService = hyperwalletSDKUserService;
		this.miraklBankAccountExtractService = miraklBankAccountExtractService;
		this.hyperwalletMiraklBankAccountMatcher = hyperwalletMiraklBankAccountMatcher;
	}

	public SellerModel synchronizeToken(final SellerModel miraklSeller) {
		final BankAccountModel miraklBankAccount = miraklSeller.getBankAccountDetails();

		if (miraklBankAccount == null || StringUtils.isBlank(miraklBankAccount.getBankAccountNumber())) {
			log.debug("Not bank account for client user id [{}], synchronization not needed",
					miraklSeller.getClientUserId());

			return miraklSeller;
		}

		// If not exact match or compatible bank account is found, the bank account token
		// in Mirakl will be updated with a null value, forcing the bank account to be
		// created again
		// in Hyperwallet
		final List<HyperwalletBankAccount> hyperwalletBankAccounts = getHwBankAccountByClientUserId(miraklSeller);
		HyperwalletBankAccount matchedHyperwalletBankAccount = hyperwalletMiraklBankAccountMatcher
				.findExactOrCompatibleMatch(hyperwalletBankAccounts, miraklBankAccount)
				.orElse(new HyperwalletBankAccount());

		if (!Objects.equals(miraklBankAccount.getToken(), matchedHyperwalletBankAccount.getToken())) {
			updateMiraklBankAccountToken(miraklSeller, matchedHyperwalletBankAccount);
		}
		return updateSellerBankAccountWithHyperwalletToken(miraklSeller, matchedHyperwalletBankAccount);
	}

	private List<HyperwalletBankAccount> getHwBankAccountByClientUserId(final SellerModel sellerModel) {

		final Hyperwallet hyperwalletSDK = hyperwalletSDKUserService
				.getHyperwalletInstanceByProgramToken(sellerModel.getProgramToken());

		try {
			HyperwalletList<HyperwalletBankAccount> bankAccounts = hyperwalletSDK
					.listBankAccounts(sellerModel.getToken());
			return bankAccounts != null && bankAccounts.getData() != null ? bankAccounts.getData() : List.of();

		}
		catch (final HyperwalletException exception) {
			log.error(
					String.format("Error while getting Hyperwallet bank account by clientUserId [%s].%n%s",
							sellerModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(exception)),
					exception);

			throw new HMCHyperwalletAPIException(exception);
		}
	}

	private void updateMiraklBankAccountToken(final SellerModel miraklSeller,
			final HyperwalletBankAccount hyperwalletBankAccount) {
		try {
			miraklBankAccountExtractService.updateBankAccountToken(miraklSeller, hyperwalletBankAccount);
		}
		catch (final MiraklApiException exception) {
			log.error("Error while updating Mirakl bank account by clientUserId [{}]", miraklSeller.getClientUserId(),
					exception);

			throw new HMCMiraklAPIException(exception);
		}
	}

	private SellerModel updateSellerBankAccountWithHyperwalletToken(final SellerModel sellerModel,
			final HyperwalletBankAccount hyperwalletBankAccount) {

		return sellerModel.toBuilder().bankAccountDetails(
				sellerModel.getBankAccountDetails().toBuilder().token(hyperwalletBankAccount.getToken()).build())
				.build();
	}

}
