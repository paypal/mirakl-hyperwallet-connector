package com.paypal.sellers.bankaccountextract.converter.impl.sellermodel;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.hyperwallet.clientsdk.model.HyperwalletUser.ProfileType;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.bankaccountextract.model.IBANBankAccountModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.model.SellerProfileType;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Class to convert from {@link IBANBankAccountModel} to {@link HyperwalletBankAccount}
 */
@Slf4j
public abstract class AbstractSellerModelToHyperwalletBankAccount
		implements Strategy<SellerModel, HyperwalletBankAccount> {

	@Override
	public HyperwalletBankAccount execute(final SellerModel source) {
		final BankAccountModel bankAccountDetails = source.getBankAccountDetails();

		if (Objects.isNull(bankAccountDetails)) {
			return null;
		}

		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setAddressLine1(bankAccountDetails.getAddressLine1());
		hyperwalletBankAccount.setAddressLine2(bankAccountDetails.getAddressLine2());
		hyperwalletBankAccount.setBankAccountId(bankAccountDetails.getBankAccountNumber());
		hyperwalletBankAccount.setTransferMethodCountry(bankAccountDetails.getTransferMethodCountry());
		hyperwalletBankAccount.setTransferMethodCurrency(bankAccountDetails.getTransferMethodCurrency());
		hyperwalletBankAccount
				.setType(HyperwalletBankAccount.Type.valueOf(bankAccountDetails.getTransferType().name()));
		hyperwalletBankAccount.setCountry(bankAccountDetails.getCountry());
		hyperwalletBankAccount.setCity(bankAccountDetails.getCity());
		hyperwalletBankAccount.setUserToken(source.getToken());
		hyperwalletBankAccount.setProfileType(ProfileType.valueOf(source.getProfileType().name()));
		hyperwalletBankAccount.setToken(bankAccountDetails.getToken());

		if (SellerProfileType.BUSINESS.equals(source.getProfileType())) {
			fillProfessionalAttributes(hyperwalletBankAccount, source);
		}
		else {
			fillIndividualAttributes(hyperwalletBankAccount, source);
		}

		return hyperwalletBankAccount;
	}

	private void fillIndividualAttributes(final HyperwalletBankAccount hyperwalletBankAccount,
			final SellerModel source) {
		final BankAccountModel bankAccountDetails = source.getBankAccountDetails();

		hyperwalletBankAccount.setFirstName(bankAccountDetails.getFirstName());
		hyperwalletBankAccount.setLastName(bankAccountDetails.getLastName());
	}

	private void fillProfessionalAttributes(final HyperwalletBankAccount hyperwalletBankAccount,
			final SellerModel source) {
		hyperwalletBankAccount.setBusinessName(source.getBusinessName());
	}

}
