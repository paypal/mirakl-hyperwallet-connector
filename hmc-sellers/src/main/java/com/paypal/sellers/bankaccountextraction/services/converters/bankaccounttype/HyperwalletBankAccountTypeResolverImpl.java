package com.paypal.sellers.bankaccountextraction.services.converters.bankaccounttype;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.support.exceptions.HMCException;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;
import com.paypal.sellers.bankaccountextraction.model.TransferType;
import com.paypal.sellers.bankaccountextraction.services.converters.currency.HyperwalletBankAccountCurrencyRestrictions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HyperwalletBankAccountTypeResolverImpl implements HyperwalletBankAccountTypeResolver {

	private final HyperwalletBankAccountCurrencyRestrictions countryCurrencyConfiguration;

	public HyperwalletBankAccountTypeResolverImpl(
			final HyperwalletBankAccountCurrencyRestrictions countryCurrencyConfiguration) {
		this.countryCurrencyConfiguration = countryCurrencyConfiguration;
	}

	@Override
	public BankAccountType getBankAccountType(final HyperwalletBankAccount hyperwalletBankAccount) {
		final HyperwalletBankAccount.Type type = hyperwalletBankAccount.getType();
		final List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> typeCandidates = countryCurrencyConfiguration
				.getEntriesFor(hyperwalletBankAccount.getTransferMethodCountry(),
						hyperwalletBankAccount.getTransferMethodCurrency(), TransferType.valueOf(type.name()));

		if (typeCandidates.isEmpty()) {
			throw new HMCException(String.format(
					"No bank account type found for country %s, currency %s and transfer type %s",
					hyperwalletBankAccount.getTransferMethodCountry(),
					hyperwalletBankAccount.getTransferMethodCurrency(), hyperwalletBankAccount.getType().name()));
		}
		else {
			return BankAccountType.valueOf(typeCandidates.get(0).getBankAccountType());
		}
	}

}
