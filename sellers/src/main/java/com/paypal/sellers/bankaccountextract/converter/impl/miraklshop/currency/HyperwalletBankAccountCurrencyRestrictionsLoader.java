package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.currency;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.sellers.bankaccountextract.model.TransferType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class creates an instance of {@link HyperwalletBankAccountCurrencyRestrictions} by
 * reading a json containing details about the constraints between bank account types,
 * countries and currency.
 */
//@formatter:off
@Configuration
public class HyperwalletBankAccountCurrencyRestrictionsLoader {

	@Bean
	public HyperwalletBankAccountCurrencyRestrictions countryCurrencyConfiguration() {
		List<HwBankAccountConstraintsJsonEntry> hyperwalletBankAccountRestrictionsJsonEntries
				= loadConfigurationEntriesFromJson();

		List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> countryCurrencyEntries
				= toCountryCurrencyEntries(hyperwalletBankAccountRestrictionsJsonEntries);

		return new HyperwalletBankAccountCurrencyRestrictions(countryCurrencyEntries);
	}

	private List<HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry> toCountryCurrencyEntries(
			List<HwBankAccountConstraintsJsonEntry> hyperwalletBankAccountRestrictionsJsonEntries) {
		// Group hyperwalletBankAccountRestrictionsJsonEntries by country and bankAccountType
		Map<String, List<HwBankAccountConstraintsJsonEntry>> groupedByCountryBankAccountType
				= hyperwalletBankAccountRestrictionsJsonEntries.stream()
				.collect(Collectors.groupingBy(this::getGroupingKey));

		return groupedByCountryBankAccountType.entrySet().stream()
				.map(this::toCountryCurrencyEntry)
				.collect(Collectors.toList());
	}

	private String getGroupingKey(HwBankAccountConstraintsJsonEntry entry) {
		return entry.getCountry() + "-" + entry.getBankAccountType();
	}

	private HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry toCountryCurrencyEntry(
			Map.Entry<String, List<HwBankAccountConstraintsJsonEntry>> countryCurrenciesMap) {
		String[] countryBankAccountType = countryCurrenciesMap.getKey().split("-");
		return new HyperwalletBankAccountCurrencyRestrictions.CountryCurrencyEntry(
				countryBankAccountType[1],
				countryBankAccountType[0],
				countryCurrenciesMap.getValue().stream()
						.map(this::toCurrencyConfigurationInfo)
						.collect(Collectors.toList()));
	}

	private HyperwalletBankAccountCurrencyInfo toCurrencyConfigurationInfo(
			HwBankAccountConstraintsJsonEntry hwBankAccountConstraintsJsonEntry) {
		return new HyperwalletBankAccountCurrencyInfo(
				hwBankAccountConstraintsJsonEntry.getCountry(),
				hwBankAccountConstraintsJsonEntry.getCurrency(),
				TransferType.valueOf(hwBankAccountConstraintsJsonEntry.getTransferType()));
	}

	private List<HwBankAccountConstraintsJsonEntry> loadConfigurationEntriesFromJson() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			InputStream inputStream = classLoader.getResourceAsStream("hwapi-bankaccount-constraints.json");
			String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper()
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			List<HwBankAccountConstraintsJsonEntry> hwBankAccountConstraintsJsonEntryList =
					mapper.readValue(json, new TypeReference<>() { });
			return filterUnsupportedConfigurations(hwBankAccountConstraintsJsonEntryList);
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Hyperwallet API Country Currency constraints file couldn't be read", e);
		}
	}

	private List<HwBankAccountConstraintsJsonEntry> filterUnsupportedConfigurations(
			List<HwBankAccountConstraintsJsonEntry> hyperwalletBankAccountRestrictionsJsonEntries) {
		return hyperwalletBankAccountRestrictionsJsonEntries.stream()
				.filter(this::isSupported)
				.collect(Collectors.toList());
	}

	private boolean isSupported(HwBankAccountConstraintsJsonEntry hwBankAccountConstraintsJsonEntry) {
		return !Objects.equals("OTHER", hwBankAccountConstraintsJsonEntry.getBankAccountType());
	}

	@Data
	@NoArgsConstructor
	private static class HwBankAccountConstraintsJsonEntry {

		private String bankAccountType;

		private String country;

		private String currency;

		private String transferType;

	}

}
