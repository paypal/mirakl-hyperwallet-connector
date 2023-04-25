package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.currency;

import com.paypal.sellers.bankaccountextract.model.TransferType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HyperwalletBankAccountCurrencyRestrictions {

	private Map<String, CountryCurrencyEntry> countryCurrencies = new HashMap<>();

	public HyperwalletBankAccountCurrencyRestrictions(List<CountryCurrencyEntry> countryCurrencyEntries) {
		countryCurrencyEntries.forEach(this::addCountryCurrencyEntry);
	}

	public List<HyperwalletBankAccountCurrencyInfo> getCurrenciesFor(String bankAccountType,
			String bankAccountCountry) {
		CountryCurrencyEntry countryCurrencyEntry = countryCurrencies.get(getKey(bankAccountType, bankAccountCountry));
		return countryCurrencyEntry != null ? countryCurrencyEntry.getSupportedCurrencies() : List.of();
	}

	public List<CountryCurrencyEntry> getEntriesFor(String country, String currency, TransferType transferType) {
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

	private void addCountryCurrencyEntry(CountryCurrencyEntry countryCurrencyEntry) {
		countryCurrencies.put(getKey(countryCurrencyEntry), countryCurrencyEntry);
	}

	private String getKey(CountryCurrencyEntry countryCurrencyEntry) {
		return getKey(countryCurrencyEntry.bankAccountType, countryCurrencyEntry.country);
	}

	private String getKey(String bankAccountType, String country) {
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
