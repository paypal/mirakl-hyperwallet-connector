package com.paypal.sellers.bankaccountextraction.services.converters.currency;

import static org.assertj.core.api.Assertions.assertThat;

import com.paypal.sellers.bankaccountextraction.model.TransferType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HyperwalletBankAccountCurrencyRestrictionsLoaderTest {

	private final HyperwalletBankAccountCurrencyRestrictionsLoader testObj = new HyperwalletBankAccountCurrencyRestrictionsLoader();

	@Test
	void countryCurrencyConfiguration_shouldReturnCountryCurrencyConfiguration() {
		// When
		final HyperwalletBankAccountCurrencyRestrictions result = testObj.countryCurrencyConfiguration();

		// Then
		// We simply check the expected size and some well known entries
		assertThat(result.numEntries()).isEqualTo(223);
		assertThat(result.getCurrenciesFor("ABA", "US")).size().isEqualTo(2);
		assertThat(result.getCurrenciesFor("ABA", "US").get(0).getCurrency()).isEqualTo("USD");
		assertThat(result.getCurrenciesFor("ABA", "US").get(1).getCurrency()).isEqualTo("USD");
		assertThat(result.getCurrenciesFor("UK", "GB")).size().isEqualTo(1);
		assertThat(result.getCurrenciesFor("UK", "GB").getFirst().getCurrency()).isEqualTo("GBP");
		assertThat(result.getCurrenciesFor("UK", "GB").getFirst().getTransferType())
			.isEqualTo(TransferType.BANK_ACCOUNT);
		assertThat(result.getCurrenciesFor("IBAN", "GB")).size().isEqualTo(2);
		assertThat(result.getCurrenciesFor("IBAN", "GB").get(0).getCurrency()).isEqualTo("EUR");
		assertThat(result.getCurrenciesFor("IBAN", "GB").get(0).getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
		assertThat(result.getCurrenciesFor("IBAN", "GB").get(1).getCurrency()).isEqualTo("USD");
		assertThat(result.getCurrenciesFor("IBAN", "GB").get(1).getTransferType()).isEqualTo(TransferType.WIRE_ACCOUNT);
	}

}
