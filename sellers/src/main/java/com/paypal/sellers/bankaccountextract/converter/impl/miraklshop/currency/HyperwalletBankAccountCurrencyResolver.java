package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.currency;

public interface HyperwalletBankAccountCurrencyResolver {

	HyperwalletBankAccountCurrencyInfo getCurrencyForCountry(String bankAccountType, String countryIsoCode,
			String shopCurrency);

}
