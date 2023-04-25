package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.bankaccounttype;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.exceptions.HMCException;
import com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.currency.HyperwalletBankAccountCurrencyRestrictions;
import com.paypal.sellers.bankaccountextract.model.BankAccountType;
import com.paypal.sellers.bankaccountextract.model.TransferType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HyperwalletBankAccountTypeResolverImpl implements HyperwalletBankAccountTypeResolver {

	private final HyperwalletBankAccountCurrencyRestrictions countryCurrencyConfiguration;

	public HyperwalletBankAccountTypeResolverImpl(
			HyperwalletBankAccountCurrencyRestrictions countryCurrencyConfiguration) {
		this.countryCurrencyConfiguration = countryCurrencyConfiguration;
	}

	@Override
	public BankAccountType getBankAccountType(HyperwalletBankAccount hyperwalletBankAccount) {
		HyperwalletBankAccount.Type type = hyperwalletBankAccount.getType();
		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> typeCandidates = countryCurrencyConfiguration
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
