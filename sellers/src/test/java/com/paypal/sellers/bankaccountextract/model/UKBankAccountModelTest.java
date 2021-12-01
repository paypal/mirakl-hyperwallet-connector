package com.paypal.sellers.bankaccountextract.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class UKBankAccountModelTest {

	@Test
	void setCountry_shouldConvertTo2LettersWhenCountry3IsocodeExists() {
		final UKBankAccountModel testObj = UKBankAccountModel.builder().country("USA").build();

		assertThat(testObj.getCountry()).isEqualTo("US");
	}

	@Test
	void setBankAccountId_shouldSetBankAccountId() {
		final UKBankAccountModel testObj = UKBankAccountModel.builder().bankAccountId("bankAccountId").build();

		assertThat(testObj.getBankAccountId()).isEqualTo("bankAccountId");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setCountry_shouldThrowAnExceptionWhenCountry3IsocodeDoesNotExists() {
		assertThatThrownBy(() -> UKBankAccountModel.builder().country("PAY").build())
				.isInstanceOf(IllegalStateException.class).hasMessage("Country with isocode: [PAY] not valid");
	}

	@Test
	void setTransferMethodCountry_shouldConvertTo2LettersWhenCountry3IsocodeExists() {
		final UKBankAccountModel testObj = UKBankAccountModel.builder().transferMethodCountry("USA").build();

		assertThat(testObj.getTransferMethodCountry()).isEqualTo("US");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setTransferMethodCountry_shouldThrowAnExceptionWhenCountry3IsocodeDoesNotExists() {
		assertThatThrownBy(() -> UKBankAccountModel.builder().transferMethodCountry("PAY").build())
				.isInstanceOf(IllegalStateException.class).hasMessage("Country with isocode: [PAY] not valid");
	}

	@Test
	void setTransferMethodCurrency_shouldSetCurrencyIsoCodeWhenCurrencyIsoCodeIsValid() {
		final UKBankAccountModel testObj = UKBankAccountModel.builder().transferMethodCurrency("EUR").build();

		assertThat(testObj.getTransferMethodCurrency()).isEqualTo("EUR");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setTransferMethodCurrency_shouldThrowAnExceptionWhenCurrencyIsInvalid() {
		assertThatThrownBy(() -> UKBankAccountModel.builder().transferMethodCurrency("INVALID_CURRENCY").build())
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Transfer method currency with code: [INVALID_CURRENCY] not valid");
	}

}
