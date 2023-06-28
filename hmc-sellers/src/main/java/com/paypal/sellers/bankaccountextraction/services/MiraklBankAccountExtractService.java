package com.paypal.sellers.bankaccountextraction.services;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;

/**
 * Service to manipulate bank account updates in Mirakl
 */
public interface MiraklBankAccountExtractService {

	/**
	 * Updates the custom field {@code hw-bankaccount-token} with the token received after
	 * creation of the {@link HyperwalletBankAccount}
	 * @param sellerModel
	 * @param hyperwalletBankAccount
	 */
	void updateBankAccountToken(SellerModel sellerModel, HyperwalletBankAccount hyperwalletBankAccount);

}
