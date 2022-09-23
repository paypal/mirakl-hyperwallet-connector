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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Class that implements the {@link TokenSynchronizationService} interface for the
 * synchronization of tokens for bank accounts
 */
@Slf4j
@Service("bankAccountTokenSynchronizationService")
public class BankAccountTokenSynchronizationServiceImpl implements TokenSynchronizationService<SellerModel> {

	private final HyperwalletSDKUserService hyperwalletSDKUserService;

	private final MiraklBankAccountExtractService miraklBankAccountExtractService;

	public BankAccountTokenSynchronizationServiceImpl(final HyperwalletSDKUserService hyperwalletSDKUserService,
			final MiraklBankAccountExtractService miraklBankAccountExtractService) {

		this.hyperwalletSDKUserService = hyperwalletSDKUserService;
		this.miraklBankAccountExtractService = miraklBankAccountExtractService;
	}

	/**
	 * Ensures the bank account token between Hyperwallet and Mirakl is synchronized
	 * @param sellerModel that contains the seller bank account item to be synchronized
	 * @return the seller with the bank account token synchronized
	 */
	@Override
	public SellerModel synchronizeToken(final SellerModel sellerModel) {

		final BankAccountModel bankAccountModel = sellerModel.getBankAccountDetails();

		if (Objects.isNull(bankAccountModel)) {

			log.debug("Not bank account for client user id [{}], synchronization not needed",
					sellerModel.getClientUserId());

			return sellerModel;
		}

		if (StringUtils.isNotBlank(bankAccountModel.getToken())) {

			log.debug("Hyperwallet token already exists for bank account number [{}], synchronization not needed",
					bankAccountModel.getBankAccountNumber());

			return sellerModel;
		}

		final Optional<HyperwalletBankAccount> hyperwalletBankAccount = getHwBankAccount(sellerModel);

		if (hyperwalletBankAccount.isPresent()) {

			updateMiraklBankAccount(sellerModel, hyperwalletBankAccount.get());

			return updateSellerBankAccountWithHyperwalletToken(sellerModel, hyperwalletBankAccount.get());

		}
		else {

			return sellerModel;
		}
	}

	private Optional<HyperwalletBankAccount> getHwBankAccount(final SellerModel sellerModel) {

		final HyperwalletList<HyperwalletBankAccount> hyperwalletBankAccounts = getHwBankAccountByClientUserId(
				sellerModel);

		if (CollectionUtils.isEmpty(hyperwalletBankAccounts.getData())) {

			log.debug("Hyperwallet bank account for client user id [{}] not found", sellerModel.getClientUserId());

			return Optional.empty();

		}
		else {

			log.debug("Hyperwallet bank account for client user id [{}] found", sellerModel.getClientUserId());

			return Optional.of(hyperwalletBankAccounts.getData().get(0));
		}
	}

	private HyperwalletList<HyperwalletBankAccount> getHwBankAccountByClientUserId(final SellerModel sellerModel) {

		final Hyperwallet hyperwalletSDK = hyperwalletSDKUserService
				.getHyperwalletInstanceByProgramToken(sellerModel.getProgramToken());

		try {

			return hyperwalletSDK.listBankAccounts(sellerModel.getToken());

		}
		catch (final HyperwalletException exception) {

			log.error(
					String.format("Error while getting Hyperwallet bank account by clientUserId [%s].%n%s",
							sellerModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(exception)),
					exception);

			throw new HMCHyperwalletAPIException(exception);
		}
	}

	private void updateMiraklBankAccount(final SellerModel sellerModel,
			final HyperwalletBankAccount hyperwalletBankAccount) {

		try {

			miraklBankAccountExtractService.updateBankAccountToken(sellerModel, hyperwalletBankAccount);

		}
		catch (final MiraklApiException exception) {

			log.error("Error while updating Mirakl bank account by clientUserId [{}]", sellerModel.getClientUserId(),
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
