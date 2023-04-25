package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.currency;

import com.paypal.sellers.bankaccountextract.model.TransferType;
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

		HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "EUR");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("EUR");
		assertThat(result.getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnShopCurrency_whenIsSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);

		List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.BANK_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "EUR", TransferType.BANK_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);

		HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "EUR");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("EUR");
		assertThat(result.getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnFirstSupportedHyperwalletCurrency_whenShopCurrencyIsNotSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);

		List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.BANK_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "EUR", TransferType.BANK_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);

		HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "GBP");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("USD");
		assertThat(result.getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnFirstSupportedHyperwalletCurrencyIgnoringWireAccounts_whenShopCurrencyIsNotSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isAllowWireAccounts()).thenReturn(false);

		List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.WIRE_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.BANK_ACCOUNT)
		);
		List<HyperwalletBankAccountCurrencyInfo> supportedHyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.BANK_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(supportedHyperwalletBankAccountCurrencyInfos))
				.thenReturn(supportedHyperwalletBankAccountCurrencyInfos);

		HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "GBP");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("CAD");
		assertThat(result.getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnFirstSupportedHyperwalletIncludingWireAccounts_whenShopCurrencyIsNotSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isAllowWireAccounts()).thenReturn(true);

		List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.WIRE_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.WIRE_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);

		HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "GBP");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("USD");
		assertThat(result.getTransferType()).isEqualTo(TransferType.WIRE_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnShopCurrencyWithWireAccount_whenIsSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isAllowWireAccounts()).thenReturn(true);

		List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.BANK_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "EUR", TransferType.WIRE_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);

		HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "EUR");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("EUR");
		assertThat(result.getTransferType()).isEqualTo(TransferType.WIRE_ACCOUNT);
	}

	@Test
	void getCurrencyForCountry_shouldReturnPrioritizedCurrency_whenShopCurrencyIsNotSupportedInHyperwallet() {
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isEnableAutomaticCurrencySelection()).thenReturn(true);
		when(hyperwalletBankAccountCurrencyResolutionConfigurationMock.isAllowWireAccounts()).thenReturn(true);

		List<HyperwalletBankAccountCurrencyInfo> hyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.WIRE_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.WIRE_ACCOUNT)
		);
		List<HyperwalletBankAccountCurrencyInfo> prioritizedHyperwalletBankAccountCurrencyInfos = List.of(
				new HyperwalletBankAccountCurrencyInfo("ES", "CAD", TransferType.WIRE_ACCOUNT),
				new HyperwalletBankAccountCurrencyInfo("ES", "USD", TransferType.WIRE_ACCOUNT)
		);
		when(hyperwalletBankAccountCurrencyRestrictionsMock.getCurrenciesFor("IBAN", "ES"))
				.thenReturn(hyperwalletBankAccountCurrencyInfos);
		when(hyperwalletBankAccountCurrencyPriorityResolverMock.sortCurrenciesByPriority(hyperwalletBankAccountCurrencyInfos))
				.thenReturn(prioritizedHyperwalletBankAccountCurrencyInfos);

		HyperwalletBankAccountCurrencyInfo result = testObj.getCurrencyForCountry("IBAN", "ES", "GBP");

		assertThat(result.getCountry()).isEqualTo("ES");
		assertThat(result.getCurrency()).isEqualTo("CAD");
		assertThat(result.getTransferType()).isEqualTo(TransferType.WIRE_ACCOUNT);
	}

}
