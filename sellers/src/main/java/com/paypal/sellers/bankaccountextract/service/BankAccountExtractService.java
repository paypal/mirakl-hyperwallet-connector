package com.paypal.sellers.bankaccountextract.service;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.IBANBankAccountModel;

import java.util.Date;

/**
 * Service that orchestrates the flow to extract bank account sellers from mirakl and
 * create them as bank accounts into hyperwallet
 */
public interface BankAccountExtractService {

	/**
	 * Extracts the {@link IBANBankAccountModel} data from Mirakl environment
	 * @param delta Optional parameter to filter all shops that have been modified since
	 * this parameter value and creates the into hyperwallet. Returns a list
	 * {@link HyperwalletBankAccount} successfully created in hyperwallet
	 */
	void extractBankAccounts(Date delta);

}
