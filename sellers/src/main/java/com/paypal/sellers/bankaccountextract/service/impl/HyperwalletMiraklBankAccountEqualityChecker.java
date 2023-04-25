package com.paypal.sellers.bankaccountextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.*;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletMiraklBankAccountEqualityChecker {

	public boolean isSameBankAccount(HyperwalletBankAccount hyperwalletBankAccount,
			BankAccountModel miraklBankAccount) {
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

	private boolean sameCountryAndCurrency(HyperwalletBankAccount hyperwalletBankAccount,
			BankAccountModel bankAccountModel) {
		return hyperwalletBankAccount.getTransferMethodCountry().equals(bankAccountModel.getTransferMethodCountry())
				&& hyperwalletBankAccount.getTransferMethodCurrency()
						.equals(bankAccountModel.getTransferMethodCurrency());
	}

	private boolean sameBankAccountId(HyperwalletBankAccount hyperwalletBankAccount,
			BankAccountModel miraklBankAccount) {
		return compareObfuscatedBankAccountNumber(miraklBankAccount.getBankAccountNumber(),
				hyperwalletBankAccount.getBankAccountId());
	}

	private boolean sameBankIdFields(HyperwalletBankAccount hyperwalletBankAccount,
			IBANBankAccountModel ibanBankAccountModel) {
		return hyperwalletBankAccount.getBankId().equals(ibanBankAccountModel.getBankBic());
	}

	private boolean sameBankIdFields(HyperwalletBankAccount hyperwalletBankAccount,
			ABABankAccountModel abaBankAccountModel) {
		return hyperwalletBankAccount.getBranchId().equals(abaBankAccountModel.getBranchId())
				&& hyperwalletBankAccount.getBankAccountPurpose().equals(abaBankAccountModel.getBankAccountPurpose());
	}

	private boolean sameBankIdFields(HyperwalletBankAccount hyperwalletBankAccount,
			UKBankAccountModel ukBankAccountModel) {
		return hyperwalletBankAccount.getBankId().equals(ukBankAccountModel.getBankAccountId());
	}

	private boolean sameBankIdFields(HyperwalletBankAccount hyperwalletBankAccount,
			CanadianBankAccountModel canadianBankAccountModel) {
		return hyperwalletBankAccount.getBankId().equals(canadianBankAccountModel.getBankId())
				&& hyperwalletBankAccount.getBranchId().equals(canadianBankAccountModel.getBranchId());
	}

	private boolean compareObfuscatedBankAccountNumber(String plainBankAccountNumber,
			String obfuscatedBankAccountNumber) {
		String lastFourDigits = plainBankAccountNumber.substring(plainBankAccountNumber.length() - 4);
		String lastFourDigitsObfuscated = obfuscatedBankAccountNumber
				.substring(obfuscatedBankAccountNumber.length() - 4);
		return lastFourDigits.equals(lastFourDigitsObfuscated);
	}

}
