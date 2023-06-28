package com.paypal.sellers.bankaccountextraction.services.converters.currency;

public interface HyperwalletBankAccountCurrencyResolver {

	HyperwalletBankAccountCurrencyInfo getCurrencyForCountry(String bankAccountType, String countryIsoCode,
			String shopCurrency);

}
