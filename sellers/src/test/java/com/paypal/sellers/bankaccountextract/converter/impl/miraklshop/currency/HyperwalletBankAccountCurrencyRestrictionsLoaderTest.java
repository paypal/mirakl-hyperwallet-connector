package com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.currency;

import com.paypal.sellers.bankaccountextract.model.TransferType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HyperwalletBankAccountCurrencyRestrictionsLoaderTest {

	private static final String PROPERTY_PREFIX = "payment.hyperwallet.country-currencies.";

	private static final String ENV_PREFIX = "PAYMENT_HYPERWALLET_COUNTRY-CURRENCIES_";

	private HyperwalletBankAccountCurrencyRestrictionsLoader testObj = new HyperwalletBankAccountCurrencyRestrictionsLoader();

	@Test
	void countryCurrencyConfiguration_shouldReturnCountryCurrencyConfiguration() {
		// When
		final HyperwalletBankAccountCurrencyRestrictions result = testObj.countryCurrencyConfiguration();

		// Then
		// We simply check the expected size and some well known entries
		assertThat(true).isTrue();
		assertThat(result.numEntries()).isEqualTo(223);
		assertThat(result.getCurrenciesFor("ABA", "US")).size().isEqualTo(2);
		assertThat(result.getCurrenciesFor("ABA", "US").get(0).getCurrency()).isEqualTo("USD");
		assertThat(result.getCurrenciesFor("ABA", "US").get(1).getCurrency()).isEqualTo("USD");
		assertThat(result.getCurrenciesFor("UK", "GB")).size().isEqualTo(1);
		assertThat(result.getCurrenciesFor("UK", "GB").get(0).getCurrency()).isEqualTo("GBP");
		assertThat(result.getCurrenciesFor("UK", "GB").get(0).getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
		assertThat(result.getCurrenciesFor("IBAN", "GB")).size().isEqualTo(2);
		assertThat(result.getCurrenciesFor("IBAN", "GB").get(0).getCurrency()).isEqualTo("EUR");
		assertThat(result.getCurrenciesFor("IBAN", "GB").get(0).getTransferType()).isEqualTo(TransferType.BANK_ACCOUNT);
		assertThat(result.getCurrenciesFor("IBAN", "GB").get(1).getCurrency()).isEqualTo("USD");
		assertThat(result.getCurrenciesFor("IBAN", "GB").get(1).getTransferType()).isEqualTo(TransferType.WIRE_ACCOUNT);
	}

}
