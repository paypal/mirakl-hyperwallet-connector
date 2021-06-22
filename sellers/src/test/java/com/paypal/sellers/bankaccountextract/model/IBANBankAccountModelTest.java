package com.paypal.sellers.bankaccountextract.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IBANBankAccountModelTest {

	@Test
	void setCountry_shouldConvertTo2LettersWhenCountry3IsocodeExists() {
		final IBANBankAccountModel testObj = IBANBankAccountModel.builder().country("USA").build();

		assertThat(testObj.getCountry()).isEqualTo("US");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setCountry_shouldThrowAnExceptionWhenCountry3IsocodeDoesNotExists() {
		assertThatThrownBy(() -> IBANBankAccountModel.builder().country("PAY").build())
				.isInstanceOf(IllegalStateException.class).hasMessage("Country with isocode: [PAY] not valid");
	}

	@Test
	void setTransferMethodCountry_shouldConvertTo2LettersWhenCountry3IsocodeExists() {
		final IBANBankAccountModel testObj = IBANBankAccountModel.builder().transferMethodCountry("USA").build();

		assertThat(testObj.getTransferMethodCountry()).isEqualTo("US");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setTransferMethodCountry_shouldThrowAnExceptionWhenCountry3IsocodeDoesNotExists() {
		assertThatThrownBy(() -> IBANBankAccountModel.builder().transferMethodCountry("PAY").build())
				.isInstanceOf(IllegalStateException.class).hasMessage("Country with isocode: [PAY] not valid");
	}

	@Test
	void setTransferMethodCurrency_shouldSetCurrencyIsoCodeWhenCurrencyIsoCodeIsValid() {
		final IBANBankAccountModel testObj = IBANBankAccountModel.builder().transferMethodCurrency("EUR").build();

		assertThat(testObj.getTransferMethodCurrency()).isEqualTo("EUR");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setTransferMethodCurrency_shouldThrowAnExceptionWhenCurrencyIsInvalid() {
		assertThatThrownBy(() -> IBANBankAccountModel.builder().transferMethodCurrency("INVALID_CURRENCY").build())
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Transfer method currency with code: [INVALID_CURRENCY] not valid");
	}

}
