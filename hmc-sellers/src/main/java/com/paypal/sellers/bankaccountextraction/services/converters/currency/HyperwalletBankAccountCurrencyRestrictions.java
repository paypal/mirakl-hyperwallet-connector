package com.paypal.sellers.bankaccountextraction.services.converters.currency;

import com.paypal.sellers.bankaccountextraction.model.TransferType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HyperwalletBankAccountCurrencyRestrictions {

	private final Map<String, CountryCurrencyEntry> countryCurrencies = new HashMap<>();

	public HyperwalletBankAccountCurrencyRestrictions(final List<CountryCurrencyEntry> countryCurrencyEntries) {
		countryCurrencyEntries.forEach(this::addCountryCurrencyEntry);
	}

	public List<HyperwalletBankAccountCurrencyInfo> getCurrenciesFor(final String bankAccountType,
			final String bankAccountCountry) {
		final CountryCurrencyEntry countryCurrencyEntry = countryCurrencies
				.get(getKey(bankAccountType, bankAccountCountry));
		return countryCurrencyEntry != null ? countryCurrencyEntry.getSupportedCurrencies() : List.of();
	}

	public List<CountryCurrencyEntry> getEntriesFor(final String country, final String currency,
			final TransferType transferType) {
		return countryCurrencies.values().stream()
				.filter(countryCurrencyEntry -> countryCurrencyEntry.getCountry().equals(country))
				.filter(countryCurrencyEntry -> countryCurrencyEntry.getSupportedCurrencies().stream()
						.anyMatch(hyperwalletBankAccountCurrencyInfo -> hyperwalletBankAccountCurrencyInfo.getCurrency()
								.equals(currency)
								&& hyperwalletBankAccountCurrencyInfo.getTransferType().equals(transferType)))
				.collect(Collectors.toList());
	}

	public int numEntries() {
		return countryCurrencies.entrySet().size();
	}

	private void addCountryCurrencyEntry(final CountryCurrencyEntry countryCurrencyEntry) {
		countryCurrencies.put(getKey(countryCurrencyEntry), countryCurrencyEntry);
	}

	private String getKey(final CountryCurrencyEntry countryCurrencyEntry) {
		return getKey(countryCurrencyEntry.bankAccountType, countryCurrencyEntry.country);
	}

	private String getKey(final String bankAccountType, final String country) {
		return bankAccountType.toUpperCase() + "-" + country.toUpperCase();
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CountryCurrencyEntry {

		private String bankAccountType;

		private String country;

		private List<HyperwalletBankAccountCurrencyInfo> supportedCurrencies;

	}

}
