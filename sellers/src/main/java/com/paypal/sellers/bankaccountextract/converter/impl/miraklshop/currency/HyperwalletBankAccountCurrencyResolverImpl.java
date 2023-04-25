package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.currency;

import com.paypal.sellers.bankaccountextract.model.TransferType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HyperwalletBankAccountCurrencyResolverImpl implements HyperwalletBankAccountCurrencyResolver {

	private final HyperwalletBankAccountCurrencyRestrictions hyperwalletBankAccountCurrencyRestrictions;

	private final HyperwalletBankAccountCurrencyResolutionConfiguration hyperwalletBankAccountCurrencyResolutionConfiguration;

	private final HyperwalletBankAccountCurrencyPriorityResolver hyperwalletBankAccountCurrencyPriorityResolver;

	public HyperwalletBankAccountCurrencyResolverImpl(
			HyperwalletBankAccountCurrencyRestrictions hyperwalletBankAccountCurrencyRestrictions,
			HyperwalletBankAccountCurrencyResolutionConfiguration hyperwalletBankAccountCurrencyResolutionConfiguration,
			HyperwalletBankAccountCurrencyPriorityResolver hyperwalletBankAccountCurrencyPriorityResolver) {
		this.hyperwalletBankAccountCurrencyRestrictions = hyperwalletBankAccountCurrencyRestrictions;
		this.hyperwalletBankAccountCurrencyResolutionConfiguration = hyperwalletBankAccountCurrencyResolutionConfiguration;
		this.hyperwalletBankAccountCurrencyPriorityResolver = hyperwalletBankAccountCurrencyPriorityResolver;
	}

	//@formatter:off
	@Override
	public HyperwalletBankAccountCurrencyInfo getCurrencyForCountry(String bankAccountType, String countryIsoCode, String shopCurrency) {
		if (!hyperwalletBankAccountCurrencyResolutionConfiguration.isEnableAutomaticCurrencySelection()) {
			log.debug("Automatic currency selection is disabled. Falling back to shop currency {}", shopCurrency);
			return createCurrencyWithDefaults(countryIsoCode, shopCurrency);
		}

		List<HyperwalletBankAccountCurrencyInfo> currencyCandidates =
				hyperwalletBankAccountCurrencyRestrictions.getCurrenciesFor(bankAccountType, countryIsoCode);
		List<HyperwalletBankAccountCurrencyInfo> filteredCandidates =
				filterUnsupportedCurrencyCandidates(currencyCandidates);
		List<HyperwalletBankAccountCurrencyInfo> sortedCurrencyCandidates =
				hyperwalletBankAccountCurrencyPriorityResolver.sortCurrenciesByPriority(filteredCandidates);

		return chooseCurrency(countryIsoCode, sortedCurrencyCandidates, shopCurrency);
	}

	private List<HyperwalletBankAccountCurrencyInfo> filterUnsupportedCurrencyCandidates(List<HyperwalletBankAccountCurrencyInfo> currencyCandidates) {
		return currencyCandidates.stream().filter(this::supportedTransferType).collect(Collectors.toList());
	}

	private boolean supportedTransferType(HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo) {
		return hyperwalletBankAccountCurrencyInfo.getTransferType().equals(TransferType.BANK_ACCOUNT)
				|| hyperwalletBankAccountCurrencyResolutionConfiguration.isAllowWireAccounts();
	}

	private HyperwalletBankAccountCurrencyInfo chooseCurrency(
			String countryIsoCode, List<HyperwalletBankAccountCurrencyInfo> currencyCandidates, String shopCurrency) {
		if (currencyCandidates == null || currencyCandidates.isEmpty()) {
			log.warn(String.format("There are no currency candidates for country %s. Falling back to shop currency %s",
					countryIsoCode, shopCurrency));

			return createCurrencyWithDefaults(countryIsoCode, shopCurrency);
		}

		Optional<HyperwalletBankAccountCurrencyInfo> matchedCurrency =
				matchBankAccountTypeAndShopCurrency(currencyCandidates, shopCurrency)
				.or(matchShopCurrency(currencyCandidates, shopCurrency));

		return matchedCurrency.orElse(CollectionUtils.isNotEmpty(currencyCandidates) ? currencyCandidates.get(0)
				: createCurrencyWithDefaults(countryIsoCode, shopCurrency));
	}

	@NotNull
	private static HyperwalletBankAccountCurrencyInfo createCurrencyWithDefaults(String countryIsoCode, String shopCurrency) {
		return new HyperwalletBankAccountCurrencyInfo(countryIsoCode, shopCurrency, TransferType.BANK_ACCOUNT);
	}

	@NotNull
	private Supplier<Optional<? extends HyperwalletBankAccountCurrencyInfo>> matchShopCurrency(List<HyperwalletBankAccountCurrencyInfo> currencyCandidates, String shopCurrency) {
		return () -> currencyCandidates.stream()
				.filter(x -> hasSameCurrency(x, shopCurrency))
				.findFirst();
	}

	@NotNull
	private Optional<HyperwalletBankAccountCurrencyInfo> matchBankAccountTypeAndShopCurrency(List<HyperwalletBankAccountCurrencyInfo> currencyCandidates, String shopCurrency) {
		return currencyCandidates.stream()
				.filter(x -> hasSameCurrencyAndBankAccountType(x, shopCurrency))
				.findFirst();
	}

	private boolean hasSameCurrencyAndBankAccountType(
			HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo, String shopCurrency) {
		return hyperwalletBankAccountCurrencyInfo.getTransferType().equals(TransferType.BANK_ACCOUNT)
				&& hyperwalletBankAccountCurrencyInfo.getCurrency().equals(shopCurrency);
	}

	private boolean hasSameCurrency(HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo,
									String shopCurrency) {
		return hyperwalletBankAccountCurrencyInfo.getCurrency().equals(shopCurrency);
	}

}
