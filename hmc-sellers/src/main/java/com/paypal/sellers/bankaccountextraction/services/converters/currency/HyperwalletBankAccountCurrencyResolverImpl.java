package com.paypal.sellers.bankaccountextraction.services.converters.currency;

import com.paypal.sellers.bankaccountextraction.model.TransferType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HyperwalletBankAccountCurrencyResolverImpl implements HyperwalletBankAccountCurrencyResolver {

	private final HyperwalletBankAccountCurrencyRestrictions hyperwalletBankAccountCurrencyRestrictions;

	private final HyperwalletBankAccountCurrencyResolutionConfiguration hyperwalletBankAccountCurrencyResolutionConfiguration;

	private final HyperwalletBankAccountCurrencyPriorityResolver hyperwalletBankAccountCurrencyPriorityResolver;

	public HyperwalletBankAccountCurrencyResolverImpl(
			final HyperwalletBankAccountCurrencyRestrictions hyperwalletBankAccountCurrencyRestrictions,
			final HyperwalletBankAccountCurrencyResolutionConfiguration hyperwalletBankAccountCurrencyResolutionConfiguration,
			final HyperwalletBankAccountCurrencyPriorityResolver hyperwalletBankAccountCurrencyPriorityResolver) {
		this.hyperwalletBankAccountCurrencyRestrictions = hyperwalletBankAccountCurrencyRestrictions;
		this.hyperwalletBankAccountCurrencyResolutionConfiguration = hyperwalletBankAccountCurrencyResolutionConfiguration;
		this.hyperwalletBankAccountCurrencyPriorityResolver = hyperwalletBankAccountCurrencyPriorityResolver;
	}

	//@formatter:off
	@Override
	public HyperwalletBankAccountCurrencyInfo getCurrencyForCountry(final String bankAccountType, final String countryIsoCode, final String shopCurrency) {
		if (!hyperwalletBankAccountCurrencyResolutionConfiguration.isEnableAutomaticCurrencySelection()) {
			log.debug("Automatic currency selection is disabled. Falling back to shop currency {}", shopCurrency);
			return createCurrencyWithDefaults(countryIsoCode, shopCurrency);
		}

		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates =
				hyperwalletBankAccountCurrencyRestrictions.getCurrenciesFor(bankAccountType, countryIsoCode);
		final List<HyperwalletBankAccountCurrencyInfo> filteredCandidates =
				filterUnsupportedCurrencyCandidates(currencyCandidates);
		final List<HyperwalletBankAccountCurrencyInfo> sortedCurrencyCandidates =
				hyperwalletBankAccountCurrencyPriorityResolver.sortCurrenciesByPriority(filteredCandidates);

		return chooseCurrency(countryIsoCode, sortedCurrencyCandidates, shopCurrency);
	}

	private List<HyperwalletBankAccountCurrencyInfo> filterUnsupportedCurrencyCandidates(final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates) {
		return currencyCandidates.stream().filter(this::supportedTransferType).collect(Collectors.toList());
	}

	private boolean supportedTransferType(final HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo) {
		return hyperwalletBankAccountCurrencyInfo.getTransferType().equals(TransferType.BANK_ACCOUNT)
				|| hyperwalletBankAccountCurrencyResolutionConfiguration.isAllowWireAccounts();
	}

	private HyperwalletBankAccountCurrencyInfo chooseCurrency(
			final String countryIsoCode, final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates, final String shopCurrency) {
		if (currencyCandidates == null || currencyCandidates.isEmpty()) {
			log.warn(String.format("There are no currency candidates for country %s. Falling back to shop currency %s",
					countryIsoCode, shopCurrency));

			return createCurrencyWithDefaults(countryIsoCode, shopCurrency);
		}

		final Optional<HyperwalletBankAccountCurrencyInfo> matchedCurrency =
				matchBankAccountTypeAndShopCurrency(currencyCandidates, shopCurrency)
				.or(matchShopCurrency(currencyCandidates, shopCurrency));

		return matchedCurrency.orElse(CollectionUtils.isNotEmpty(currencyCandidates) ? currencyCandidates.get(0)
				: createCurrencyWithDefaults(countryIsoCode, shopCurrency));
	}

	@NotNull
	private static HyperwalletBankAccountCurrencyInfo createCurrencyWithDefaults(final String countryIsoCode, final String shopCurrency) {
		return new HyperwalletBankAccountCurrencyInfo(countryIsoCode, shopCurrency, TransferType.BANK_ACCOUNT);
	}

	@NotNull
	private Supplier<Optional<? extends HyperwalletBankAccountCurrencyInfo>> matchShopCurrency(final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates, final String shopCurrency) {
		return () -> currencyCandidates.stream()
				.filter(x -> hasSameCurrency(x, shopCurrency))
				.findFirst();
	}

	@NotNull
	private Optional<HyperwalletBankAccountCurrencyInfo> matchBankAccountTypeAndShopCurrency(final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates, final String shopCurrency) {
		return currencyCandidates.stream()
				.filter(x -> hasSameCurrencyAndBankAccountType(x, shopCurrency))
				.findFirst();
	}

	private boolean hasSameCurrencyAndBankAccountType(
			final HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo, final String shopCurrency) {
		return hyperwalletBankAccountCurrencyInfo.getTransferType().equals(TransferType.BANK_ACCOUNT)
				&& hyperwalletBankAccountCurrencyInfo.getCurrency().equals(shopCurrency);
	}

	private boolean hasSameCurrency(final HyperwalletBankAccountCurrencyInfo hyperwalletBankAccountCurrencyInfo,
									final String shopCurrency) {
		return hyperwalletBankAccountCurrencyInfo.getCurrency().equals(shopCurrency);
	}

}
