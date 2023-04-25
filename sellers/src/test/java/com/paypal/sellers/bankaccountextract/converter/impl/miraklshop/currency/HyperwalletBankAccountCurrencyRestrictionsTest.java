package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.currency;

import com.paypal.sellers.bankaccountextract.model.TransferType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HyperwalletBankAccountCurrencyRestrictionsTest {

	private HyperwalletBankAccountCurrencyRestrictions testObj;

	@Test
	void getCurrenciesFor_shouldReturnEmptyList_whenNoEntryFound() {
		// given
		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> countryCurrencyEntries = List.of();
		testObj = new HyperwalletBankAccountCurrencyRestrictions(countryCurrencyEntries);

		// when
		List<HyperwalletBankAccountCurrencyInfo> result = testObj.getCurrenciesFor("bankAccountType", "country");

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void getCurrenciesFor_shouldReturnEmptyList_whenEntryFoundButNoCurrency() {
		// given
		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> countryCurrencyEntries = List
				.of(new HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry("bankAccountType", "country",
						List.of()));
		testObj = new HyperwalletBankAccountCurrencyRestrictions(countryCurrencyEntries);

		// when
		List<HyperwalletBankAccountCurrencyInfo> result = testObj.getCurrenciesFor("bankAccountType", "country");

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void getCurrenciesFor_shouldReturnSingleEntry_whenEntryFound() {
		// given
		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> countryCurrencyEntries = List.of(
				new HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry("bankAccountType", "country",
						List.of(new HyperwalletBankAccountCurrencyInfo("country", "currency",
								TransferType.BANK_ACCOUNT))),
				new HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry("bankAccountType", "country2",
						List.of(new HyperwalletBankAccountCurrencyInfo("country2", "currency",
								TransferType.BANK_ACCOUNT))));
		testObj = new HyperwalletBankAccountCurrencyRestrictions(countryCurrencyEntries);

		// when
		List<HyperwalletBankAccountCurrencyInfo> result = testObj.getCurrenciesFor("bankAccountType", "country");

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getCurrency()).isEqualTo("currency");
		assertThat(result.get(0).getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getCurrenciesFor_shouldReturnMultipleEntries_whenEntryFound() {
		// given
		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> countryCurrencyEntries = List.of(
				new HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry("bankAccountType", "country", List
						.of(new HyperwalletBankAccountCurrencyInfo("country", "currency", TransferType.BANK_ACCOUNT),
								new HyperwalletBankAccountCurrencyInfo("country", "currency2",
										TransferType.BANK_ACCOUNT))),
				new HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry("bankAccountType", "country2",
						List.of(new HyperwalletBankAccountCurrencyInfo("country2", "currency",
								TransferType.BANK_ACCOUNT))));
		testObj = new HyperwalletBankAccountCurrencyRestrictions(countryCurrencyEntries);

		// when
		List<HyperwalletBankAccountCurrencyInfo> result = testObj.getCurrenciesFor("bankAccountType", "country");

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getCurrency()).isEqualTo("currency");
		assertThat(result.get(0).getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
		assertThat(result.get(1).getCurrency()).isEqualTo("currency2");
		assertThat(result.get(1).getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getEntriesFor_shouldReturnEmptyList_whenNoEntryFound() {
		// given
		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> countryCurrencyEntries = List.of();
		testObj = new HyperwalletBankAccountCurrencyRestrictions(countryCurrencyEntries);

		// when
		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> result = testObj
				.getEntriesFor("bankAccuntCountry", "currency", TransferType.BANK_ACCOUNT);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void getEntriesFor_shouldReturnEntries_whenEntryFound() {
		// given
		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> countryCurrencyEntries = List.of(
				new HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry("bankAccountType", "country", List
						.of(new HyperwalletBankAccountCurrencyInfo("country", "currency", TransferType.BANK_ACCOUNT),
								new HyperwalletBankAccountCurrencyInfo("country", "currency2",
										TransferType.BANK_ACCOUNT))),
				new HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry("bankAccountType", "country2",
						List.of(new HyperwalletBankAccountCurrencyInfo("country2", "currency",
								TransferType.BANK_ACCOUNT))));
		testObj = new HyperwalletBankAccountCurrencyRestrictions(countryCurrencyEntries);

		// when
		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> result = testObj.getEntriesFor("country",
				"currency", TransferType.BANK_ACCOUNT);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getBankAccountType()).isEqualTo("bankAccountType");
		assertThat(result.get(0).getCountry()).isEqualTo("country");
		assertThat(result.get(0).getSupportedCurrencies()).hasSize(2);
		assertThat(result.get(0).getSupportedCurrencies().get(0).getCountry()).isEqualTo("country");
		assertThat(result.get(0).getSupportedCurrencies().get(0).getCurrency()).isEqualTo("currency");
		assertThat(result.get(0).getSupportedCurrencies().get(0).getTransferType())
				.isEqualTo(TransferType.BANK_ACCOUNT);
		assertThat(result.get(0).getSupportedCurrencies().get(1).getCountry()).isEqualTo("country");
		assertThat(result.get(0).getSupportedCurrencies().get(1).getCurrency()).isEqualTo("currency2");
		assertThat(result.get(0).getSupportedCurrencies().get(1).getTransferType())
				.isEqualTo(TransferType.BANK_ACCOUNT);
	}

}
