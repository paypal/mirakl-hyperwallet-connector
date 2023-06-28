package com.paypal.sellers.bankaccountextraction.services.converters.currency;

import com.paypal.sellers.bankaccountextraction.model.TransferType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

//@formatter:off
@ExtendWith(MockitoExtension.class)
class HyperwalletBankAccountCurrencyPriorityResolverTest {

	@InjectMocks
	private HyperwalletBankAccountCurrencyPriorityResolver testObj;

	@Mock
	private HyperwalletBankAccountCurrencyResolutionConfiguration hyperwalletBankAccountCurrencyResolutionConfigurationMock;

	@Test
	void sortCurrenciesByPriority_shouldReturnEmptyList_whenCurrencyCandidatesIsNull() {
		//given
		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates = null;

		//when
		final List<HyperwalletBankAccountCurrencyInfo> result = testObj.sortCurrenciesByPriority(currencyCandidates);

		//then
		assertThat(result).isEmpty();
	}

	@Test
	void sortCurrenciesByPriority_shouldReturnEmptyList_whenCurrencyCandidatesIsEmpty() {
		//given
		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates = List.of();

		//when
		final List<HyperwalletBankAccountCurrencyInfo> result = testObj.sortCurrenciesByPriority(currencyCandidates);

		//then
		assertThat(result).isEmpty();
	}

	@Test
	void sortCurrenciesByPriority_shouldReturnOriginalPriorities_whenPrioritizeFlagsAreDisabled() {
		//given
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isOverrideCurrencySelectionPriority()).thenReturn(false);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isPrioritizeBankAccountTypeOverCurrency()).thenReturn(false);
		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates = currencies(
				"GBP", TransferType.WIRE_ACCOUNT,
				"EUR", TransferType.BANK_ACCOUNT,
				"USD", TransferType.WIRE_ACCOUNT
		);

		//when
		final List<HyperwalletBankAccountCurrencyInfo> result = testObj.sortCurrenciesByPriority(currencyCandidates);

		//then
		assertSortedList(result, currencies("GBP", TransferType.WIRE_ACCOUNT,
				"EUR", TransferType.BANK_ACCOUNT,
				"USD", TransferType.WIRE_ACCOUNT));
		assertThat(result).hasSize(3);
	}

	@Test
	void sortCurrenciesByPriority_shouldPrioritizeBankAccountTransferType_whenPrioritizeTransferTypeOverCurrencyFlagIsEnabled() {
		//given
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isOverrideCurrencySelectionPriority()).thenReturn(false);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isPrioritizeBankAccountTypeOverCurrency()).thenReturn(true);
		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates = currencies(
				"GBP", TransferType.WIRE_ACCOUNT,
				"EUR", TransferType.BANK_ACCOUNT,
				"USD", TransferType.WIRE_ACCOUNT
		);

		//when
		final List<HyperwalletBankAccountCurrencyInfo> result = testObj.sortCurrenciesByPriority(currencyCandidates);

		//then
		assertSortedList(result, currencies("EUR", TransferType.BANK_ACCOUNT,
				"GBP", TransferType.WIRE_ACCOUNT,
				"USD", TransferType.WIRE_ACCOUNT));
		assertThat(result).hasSize(3);
	}

	@Test
	void sortCurrenciesByPriority_shouldPrioritizeBankAccountTransferTypeOnlyForSameCurrency_whenPrioritizeTransferTypeOverCurrencyFlagIsDisabled() {
		//given
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isOverrideCurrencySelectionPriority()).thenReturn(false);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isPrioritizeBankAccountTypeOverCurrency()).thenReturn(false);
		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates = currencies(
				"GBP", TransferType.WIRE_ACCOUNT,
				"EUR", TransferType.WIRE_ACCOUNT,
				"EUR", TransferType.BANK_ACCOUNT,
				"USD", TransferType.WIRE_ACCOUNT);

		//when
		final List<HyperwalletBankAccountCurrencyInfo> result = testObj.sortCurrenciesByPriority(currencyCandidates);

		//then
		assertSortedList(result, currencies("GBP", TransferType.WIRE_ACCOUNT,
				"EUR", TransferType.BANK_ACCOUNT,
				"EUR", TransferType.WIRE_ACCOUNT,
				"USD", TransferType.WIRE_ACCOUNT));
	}

	@Test
	void sortCurrenciesByPriority_prioritizeTransferTypeShouldNotChangeRelativeCurrencyOrder() {
		//given
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isOverrideCurrencySelectionPriority()).thenReturn(false);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isPrioritizeBankAccountTypeOverCurrency()).thenReturn(true);
		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates = currencies(
				"GBP", TransferType.WIRE_ACCOUNT,
				"GBP", TransferType.BANK_ACCOUNT,
				"EUR", TransferType.WIRE_ACCOUNT,
				"EUR", TransferType.BANK_ACCOUNT,
				"USD", TransferType.BANK_ACCOUNT);

		//when
		final List<HyperwalletBankAccountCurrencyInfo> result = testObj.sortCurrenciesByPriority(currencyCandidates);

		//then
		assertSortedList(result, currencies(
				"GBP", TransferType.BANK_ACCOUNT,
				"EUR", TransferType.BANK_ACCOUNT,
				"USD", TransferType.BANK_ACCOUNT,
				"GBP", TransferType.WIRE_ACCOUNT,
				"EUR", TransferType.WIRE_ACCOUNT));
	}

	@Test
	void sortCurrenciesByPriority_shouldFollowGlobalCurrencyPriority_whenOverrideCurrencyPriorityIsEnabledAndNoPerCountryPrioritiesExist() {
		//given
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isOverrideCurrencySelectionPriority()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isPrioritizeBankAccountTypeOverCurrency()).thenReturn(false);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.getGlobalCurrencyPriority()).thenReturn(List.of("EUR", "GBP", "USD"));
		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates = currenciesWithCountry(
				"ES", "USD", TransferType.BANK_ACCOUNT,
				"ES", "CAD", TransferType.BANK_ACCOUNT,
				"ES", "EUR", TransferType.BANK_ACCOUNT,
				"UK", "GBP", TransferType.BANK_ACCOUNT,
				"UK", "USD", TransferType.BANK_ACCOUNT);


		//when
		final List<HyperwalletBankAccountCurrencyInfo> result = testObj.sortCurrenciesByPriority(currencyCandidates);

		//then
		assertSortedList(result, currenciesWithCountry(
				"ES", "EUR", TransferType.BANK_ACCOUNT,
				"ES", "USD", TransferType.BANK_ACCOUNT,
				"ES", "CAD", TransferType.BANK_ACCOUNT,
				"UK", "GBP", TransferType.BANK_ACCOUNT,
				"UK", "USD", TransferType.BANK_ACCOUNT));
	}

	@Test
	void sortCurrenciesByPriority_shouldFollowPerCountryCurrencyPriority_whenOverrideCurrencyPriorityIsEnabledAndPerCountryPrioritiesExist() {
		//given
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isOverrideCurrencySelectionPriority()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isPrioritizeBankAccountTypeOverCurrency()).thenReturn(false);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.getGlobalCurrencyPriority()).thenReturn(List.of("EUR", "GBP", "USD"));
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.getPerCountryCurrencyPriority()).thenReturn(Map.of("UK", List.of("USD", "GBP")));
		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates = currenciesWithCountry(
				"ES", "USD", TransferType.BANK_ACCOUNT,
				"ES", "CAD", TransferType.BANK_ACCOUNT,
				"ES", "EUR", TransferType.BANK_ACCOUNT,
				"UK", "GBP", TransferType.BANK_ACCOUNT,
				"UK", "USD", TransferType.BANK_ACCOUNT);


		//when
		final List<HyperwalletBankAccountCurrencyInfo> result = testObj.sortCurrenciesByPriority(currencyCandidates);

		//then
		assertSortedList(result, currenciesWithCountry(
				"ES", "EUR", TransferType.BANK_ACCOUNT,
				"ES", "USD", TransferType.BANK_ACCOUNT,
				"ES", "CAD", TransferType.BANK_ACCOUNT,
				"UK", "USD", TransferType.BANK_ACCOUNT,
				"UK", "GBP", TransferType.BANK_ACCOUNT));
	}

	@Test
	void sortCurrenciesByPriority_shouldFollowPerCountryCurrencyPriority_whenOverrideCurrencyPriorityIsEnabledAndPerCountryPrioritiesExist2() {
		//given
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isOverrideCurrencySelectionPriority()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isPrioritizeBankAccountTypeOverCurrency()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.getGlobalCurrencyPriority()).thenReturn(List.of("EUR", "GBP", "USD"));
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.getPerCountryCurrencyPriority()).thenReturn(Map.of("UK", List.of("USD", "GBP")));
		final List<HyperwalletBankAccountCurrencyInfo> currencyCandidates = currenciesWithCountry(
				"ES", "USD", TransferType.BANK_ACCOUNT,
				"ES", "CAD", TransferType.BANK_ACCOUNT,
				"ES", "EUR", TransferType.WIRE_ACCOUNT,
				"UK", "GBP", TransferType.BANK_ACCOUNT,
				"UK", "USD", TransferType.BANK_ACCOUNT);


		//when
		final List<HyperwalletBankAccountCurrencyInfo> result = testObj.sortCurrenciesByPriority(currencyCandidates);

		//then
		assertSortedList(result, currenciesWithCountry(
				"ES", "USD", TransferType.BANK_ACCOUNT,
				"ES", "CAD", TransferType.BANK_ACCOUNT,
				"ES", "EUR", TransferType.WIRE_ACCOUNT,
				"UK", "USD", TransferType.BANK_ACCOUNT,
				"UK", "GBP", TransferType.BANK_ACCOUNT));
	}

	private static List<HyperwalletBankAccountCurrencyInfo> currencies(final Object... args) {
		// Create a list of HyperwalletBankAccountCurrencyInfo with a fixed "COUNTRY" country by
		// iterating over the args array so we take an item as the currency and the next consecutive item
		// as the transfer type.
		return IntStream.range(0, args.length)
				.filter(i -> i % 2 == 0)
				.mapToObj(i -> new HyperwalletBankAccountCurrencyInfo("COUNTRY", (String) args[i], (TransferType) args[i + 1]))
				.collect(Collectors.toList());
	}

	private static List<HyperwalletBankAccountCurrencyInfo> currenciesWithCountry(final Object... args) {
		return IntStream.range(0, args.length)
				.filter(i -> i % 3 == 0)
				.mapToObj(i -> new HyperwalletBankAccountCurrencyInfo((String) args[i], (String) args[i + 1], (TransferType) args[i + 2]))
				.collect(Collectors.toList());
	}

	private void assertSortedList(final List<HyperwalletBankAccountCurrencyInfo> result, final List<HyperwalletBankAccountCurrencyInfo> expected) {
		assertThat(result).hasSize(expected.size());
		for (int i = 0; i < result.size(); i++) {
			assertThat(result.get(i).getCountry()).isEqualTo(expected.get(i).getCountry());
			assertThat(result.get(i).getCurrency()).isEqualTo(expected.get(i).getCurrency());
			assertThat(result.get(i).getTransferType()).isEqualTo(expected.get(i).getTransferType());
		}
	}

}
