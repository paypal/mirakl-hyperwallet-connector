package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.bankaccounttype;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.BankAccountType;

public interface HyperwalletBankAccountTypeResolver {

	BankAccountType getBankAccountType(HyperwalletBankAccount hyperwalletBankAccount);

}
