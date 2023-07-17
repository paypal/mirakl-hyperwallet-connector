package com.paypal.sellers.bankaccountextraction.services.converters.currency;

import com.paypal.sellers.bankaccountextraction.model.TransferType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

//@formatter:off
@ExtendWith(MockitoExtension.class)
class HyperwalletBankAccountCurrencyResolverImplTest {

	@InjectMocks
	private HyperwalletBankAccountCurrencyResolverImpl testObj;

	@Mock
	private HyperwalletBankAccountCurrencyRestrictions hyperwalletBankAccountCurrencyRestrictionsMock;

	@Mock
	private HyperwalletBankAccountCurrencyResolutionConfiguration hyperwalletBankAccountCurrencyResolutionConfigurationMock;

	@Mock
	private HyperwalletBankAccountCurrencyPriorityResolver hyperwalletBankAccountCurrencyPriorityResolverMock;

	@Test
	void getCurrencyForCountry_shouldReturnShopCurrency_whenAutomaticCurrencySelectionIsDisabled() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(false);

		final HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "EUR");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("EUR");
		assertThat(result.getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnShopCurrency_whenIsSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);

		final List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.BANK_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "EUR", TransferType.BANK_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);

		final HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "EUR");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("EUR");
		assertThat(result.getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnFirstSupportedHyperwalletCurrency_whenShopCurrencyIsNotSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);

		final List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.BANK_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "EUR", TransferType.BANK_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);

		final HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "GBP");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("USD");
		assertThat(result.getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnFirstSupportedHyperwalletCurrencyIgnoringWireAccounts_whenShopCurrencyIsNotSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isAllowWireAccounts()).thenReturn(false);

		final List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.WIRE_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.BANK_ACCOUNT)
		);
		final List<HyperwalletBankAccountCurrencyInfo> supportedHyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.BANK_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(supportedHyperwalletBankAccountCurrencyInfos))
				.thenReturn(supportedHyperwalletBankAccountCurrencyInfos);

		final HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "GBP");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("CAD");
		assertThat(result.getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnFirstSupportedHyperwalletIncludingWireAccounts_whenShopCurrencyIsNotSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isAllowWireAccounts()).thenReturn(true);

		final List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.WIRE_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.WIRE_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);

		final HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "GBP");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("USD");
		assertThat(result.getTransferType()).isEqualTo(TransferType.WIRE_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnShopCurrencyWithWireAccount_whenIsSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isAllowWireAccounts()).thenReturn(true);

		final List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.BANK_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "EUR", TransferType.WIRE_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);

		final HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "EUR");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("EUR");
		assertThat(result.getTransferType()).isEqualTo(TransferType.WIRE_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnPrioritizedCurrency_whenShopCurrencyIsNotSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isAllowWireAccounts()).thenReturn(true);

		final List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.WIRE_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.WIRE_ACCOUNT)
		);
		final List<HyperwalletBankAccountCurrencyInfo> prioritizedHyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.WIRE_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.WIRE_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(prioritizedHyperwalletBankAccountCurrencyInfos);

		final HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "GBP");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("CAD");
		assertThat(result.getTransferType()).isEqualTo(TransferType.WIRE_ACCOUNT);
	}

}
