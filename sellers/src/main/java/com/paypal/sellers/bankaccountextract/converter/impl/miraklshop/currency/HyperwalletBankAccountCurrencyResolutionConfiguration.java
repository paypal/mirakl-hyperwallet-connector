package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.currency;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Component
public class HyperwalletBankAccountCurrencyResolutionConfiguration {

	@Value("${sellers.bankaccounts.enableAutomaticCurrencySelection}")
	protected boolean enableAutomaticCurrencySelection;

	@Value("${sellers.bankaccounts.allowWireAccountTransferType}")
	protected boolean allowWireAccounts;

	@Value("${sellers.bankaccounts.overrideCurrencySelectionPriority}")
	protected boolean overrideCurrencySelectionPriority;

	@Value("${sellers.bankaccounts.prioritizeBankAccountTypeOverCurrency}")
	protected boolean prioritizeBankAccountTypeOverCurrency;

	protected List<String> globalCurrencyPriority;

	protected Map<String, List<String>> perCountryCurrencyPriority;

	public HyperwalletBankAccountCurrencyResolutionConfiguration(
			@Value("${sellers.bankaccounts.overriddenCurrencySelectionPriority}") String overriddenCurrencySelectionPriority) {
		this.globalCurrencyPriority = buildGlobalCurrencySelectionPriority(overriddenCurrencySelectionPriority);
		this.perCountryCurrencyPriority = buildPerCountryCurrencyPriority(overriddenCurrencySelectionPriority);
	}

	protected Map<String, List<String>> buildPerCountryCurrencyPriority(String perCountryCurrencySelectionPriority) {
		// Country and currency list separated by semicolons. Expected format:
		// "US:USD,GBP;CA:CAD,USD"
		return Stream.of(perCountryCurrencySelectionPriority.split(";")).map(it -> it.split(":"))
				.filter(it -> it.length == 2)
				.collect(Collectors.toMap(it -> it[0], it -> parseGlobalCurrencyPriority(it[1])));
	}

	protected List<String> buildGlobalCurrencySelectionPriority(String perCountryCurrencySelectionPriority) {
		// Country and currency list separated by semicolons. Expected format:
		// "US:USD,GBP;CA:CAD,USD"
		return Stream.of(perCountryCurrencySelectionPriority.split(";")).map(it -> it.split(":"))
				.filter(it -> it.length == 1).findFirst().map(it -> parseGlobalCurrencyPriority(it[0]))
				.orElse(List.of());
	}

	protected List<String> parseGlobalCurrencyPriority(String currencySelectionPriority) {
		// Currency list separated by comma. Expected format example: "USD,GBP"
		return Stream.of(currencySelectionPriority.split(",")).map(String::trim).collect(Collectors.toList());
	}

}
