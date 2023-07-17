package com.paypal.sellers.bankaccountextraction.services;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.bankaccountextraction.services.converters.bankaccounttype.HyperwalletBankAccountTypeResolver;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HyperwalletMiraklBankAccountCompatibilityChecker {

	private final HyperwalletBankAccountTypeResolver hyperwalletBankAccountTypeResolver;

	public HyperwalletMiraklBankAccountCompatibilityChecker(
			final HyperwalletBankAccountTypeResolver hyperwalletBankAccountTypeResolver) {
		this.hyperwalletBankAccountTypeResolver = hyperwalletBankAccountTypeResolver;
	}

	public boolean isBankAccountCompatible(final HyperwalletBankAccount hyperwalletBankAccount,
			final BankAccountModel miraklBankAccount) {
		return hasSameBankAccountType(hyperwalletBankAccount, miraklBankAccount)
				&& hasSameBankAccountCountry(hyperwalletBankAccount, miraklBankAccount);
	}

	private boolean hasSameBankAccountType(final HyperwalletBankAccount hyperwalletBankAccount,
			final BankAccountModel miraklBankAccount) {
		return hyperwalletBankAccountTypeResolver.getBankAccountType(hyperwalletBankAccount) == miraklBankAccount
				.getType();
	}

	private boolean hasSameBankAccountCountry(final HyperwalletBankAccount hyperwalletBankAccount,
			final BankAccountModel miraklBankAccount) {
		return Objects.equals(hyperwalletBankAccount.getTransferMethodCountry(),
				miraklBankAccount.getTransferMethodCountry());
	}

}
