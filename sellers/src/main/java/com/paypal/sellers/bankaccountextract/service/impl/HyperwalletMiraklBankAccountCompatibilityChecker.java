package com.paypal.sellers.bankaccountextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.bankaccounttype.HyperwalletBankAccountTypeResolver;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HyperwalletMiraklBankAccountCompatibilityChecker {

	private final HyperwalletBankAccountTypeResolver hyperwalletBankAccountTypeResolver;

	public HyperwalletMiraklBankAccountCompatibilityChecker(
			HyperwalletBankAccountTypeResolver hyperwalletBankAccountTypeResolver) {
		this.hyperwalletBankAccountTypeResolver = hyperwalletBankAccountTypeResolver;
	}

	public boolean isBankAccountCompatible(HyperwalletBankAccount hyperwalletBankAccount,
			BankAccountModel miraklBankAccount) {
		return hasSameBankAccountType(hyperwalletBankAccount, miraklBankAccount)
				&& hasSameBankAccountCountry(hyperwalletBankAccount, miraklBankAccount);
	}

	private boolean hasSameBankAccountType(HyperwalletBankAccount hyperwalletBankAccount,
			BankAccountModel miraklBankAccount) {
		return hyperwalletBankAccountTypeResolver.getBankAccountType(hyperwalletBankAccount) == miraklBankAccount
				.getType();
	}

	private boolean hasSameBankAccountCountry(HyperwalletBankAccount hyperwalletBankAccount,
			BankAccountModel miraklBankAccount) {
		return Objects.equals(hyperwalletBankAccount.getTransferMethodCountry(),
				miraklBankAccount.getTransferMethodCountry());
	}

}
