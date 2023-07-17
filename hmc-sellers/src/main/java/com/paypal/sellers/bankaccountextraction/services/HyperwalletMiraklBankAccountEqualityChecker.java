package com.paypal.sellers.bankaccountextraction.services;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.*;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletMiraklBankAccountEqualityChecker {

	public boolean isSameBankAccount(final HyperwalletBankAccount hyperwalletBankAccount,
			final BankAccountModel miraklBankAccount) {
		if (!sameCountryAndCurrency(hyperwalletBankAccount, miraklBankAccount)
				|| !sameBankAccountId(hyperwalletBankAccount, miraklBankAccount)) {
			return false;
		}

		if (miraklBankAccount instanceof IBANBankAccountModel) {
			return sameBankIdFields(hyperwalletBankAccount, (IBANBankAccountModel) miraklBankAccount);
		}
		else if (miraklBankAccount instanceof ABABankAccountModel) {
			return sameBankIdFields(hyperwalletBankAccount, (ABABankAccountModel) miraklBankAccount);
		}
		else if (miraklBankAccount instanceof UKBankAccountModel) {
			return sameBankIdFields(hyperwalletBankAccount, (UKBankAccountModel) miraklBankAccount);
		}
		else if (miraklBankAccount instanceof CanadianBankAccountModel) {
			return sameBankIdFields(hyperwalletBankAccount, (CanadianBankAccountModel) miraklBankAccount);
		}
		else {
			throw new IllegalArgumentException(
					"Bank account type not supported: " + miraklBankAccount.getClass().getName());
		}
	}

	private boolean sameCountryAndCurrency(final HyperwalletBankAccount hyperwalletBankAccount,
			final BankAccountModel bankAccountModel) {
		return hyperwalletBankAccount.getTransferMethodCountry().equals(bankAccountModel.getTransferMethodCountry())
				&& hyperwalletBankAccount.getTransferMethodCurrency()
						.equals(bankAccountModel.getTransferMethodCurrency());
	}

	private boolean sameBankAccountId(final HyperwalletBankAccount hyperwalletBankAccount,
			final BankAccountModel miraklBankAccount) {
		return compareObfuscatedBankAccountNumber(miraklBankAccount.getBankAccountNumber(),
				hyperwalletBankAccount.getBankAccountId());
	}

	private boolean sameBankIdFields(final HyperwalletBankAccount hyperwalletBankAccount,
			final IBANBankAccountModel ibanBankAccountModel) {
		return hyperwalletBankAccount.getBankId().equals(ibanBankAccountModel.getBankBic());
	}

	private boolean sameBankIdFields(final HyperwalletBankAccount hyperwalletBankAccount,
			final ABABankAccountModel abaBankAccountModel) {
		return hyperwalletBankAccount.getBranchId().equals(abaBankAccountModel.getBranchId())
				&& hyperwalletBankAccount.getBankAccountPurpose().equals(abaBankAccountModel.getBankAccountPurpose());
	}

	private boolean sameBankIdFields(final HyperwalletBankAccount hyperwalletBankAccount,
			final UKBankAccountModel ukBankAccountModel) {
		return hyperwalletBankAccount.getBankId().equals(ukBankAccountModel.getBankAccountId());
	}

	private boolean sameBankIdFields(final HyperwalletBankAccount hyperwalletBankAccount,
			final CanadianBankAccountModel canadianBankAccountModel) {
		return hyperwalletBankAccount.getBankId().equals(canadianBankAccountModel.getBankId())
				&& hyperwalletBankAccount.getBranchId().equals(canadianBankAccountModel.getBranchId());
	}

	private boolean compareObfuscatedBankAccountNumber(final String plainBankAccountNumber,
			final String obfuscatedBankAccountNumber) {
		final String lastFourDigits = plainBankAccountNumber.substring(plainBankAccountNumber.length() - 4);
		final String lastFourDigitsObfuscated = obfuscatedBankAccountNumber
				.substring(obfuscatedBankAccountNumber.length() - 4);
		return lastFourDigits.equals(lastFourDigitsObfuscated);
	}

}
