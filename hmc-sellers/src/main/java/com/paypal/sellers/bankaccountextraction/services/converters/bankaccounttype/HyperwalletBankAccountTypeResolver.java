package com.paypal.sellers.bankaccountextraction.services.converters.bankaccounttype;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;

public interface HyperwalletBankAccountTypeResolver {

	BankAccountType getBankAccountType(HyperwalletBankAccount hyperwalletBankAccount);

}
