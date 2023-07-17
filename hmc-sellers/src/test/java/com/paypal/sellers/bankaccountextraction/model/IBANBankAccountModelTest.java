package com.paypal.sellers.bankaccountextraction.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;
import com.paypal.sellers.bankaccountextraction.model.IBANBankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.TransferType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IBANBankAccountModelTest {

	private static final String TOKEN_VALUE_1 = "token1";

	private static final String TOKEN_VALUE_2 = "token2";

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

	@Test
	void toBuild_ShouldReturnCopyOfIBANBankAccountModel() {

		final IBANBankAccountModel bankAccountModel = createIBANBankAccountModelObject(TOKEN_VALUE_1);
		final IBANBankAccountModel copyBankAccountModel = bankAccountModel.toBuilder().build();

		assertThat(bankAccountModel).isEqualTo(copyBankAccountModel);
	}

	@Test
	void toBuild_ShouldReturnCopyOfIBANBankAccountModelWithTheUpdatedToken() {

		final IBANBankAccountModel bankAccountModel1 = createIBANBankAccountModelObject(TOKEN_VALUE_1);
		final IBANBankAccountModel bankAccountModel2 = createIBANBankAccountModelObject(TOKEN_VALUE_2);
		final IBANBankAccountModel copyBankAccountModel = bankAccountModel1.toBuilder().token(TOKEN_VALUE_2).build();

		assertThat(bankAccountModel2).isEqualTo(copyBankAccountModel);
	}

	private IBANBankAccountModel createIBANBankAccountModelObject(final String token) {
		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue tokenBankAccountField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		tokenBankAccountField.setCode(HYPERWALLET_BANK_ACCOUNT_TOKEN);
		tokenBankAccountField.setValue(token);

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue stateProvinceBusinessStakeHolderField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		stateProvinceBusinessStakeHolderField.setCode(HYPERWALLET_BANK_ACCOUNT_STATE);
		stateProvinceBusinessStakeHolderField.setValue("stateProvince");

		final MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue hyperwalletProgramAccountField = new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue();
		hyperwalletProgramAccountField.setCode(HYPERWALLET_PROGRAM);
		hyperwalletProgramAccountField.setValue("hyperwalletProgram");

		//@formatter:off
		return IBANBankAccountModel.builder()
				.transferMethodCountry("USA")
				.transferMethodCurrency("EUR")
				.transferType(TransferType.BANK_ACCOUNT)
				.type(BankAccountType.ABA)
				.bankAccountNumber("111")
				.businessName("businessName")
				.firstName("firstName")
				.lastName("lastName")
				.country("USA")
				.addressLine1("addressLine1")
				.addressLine2("addressLine2")
				.city("city")
				.stateProvince(List.of(stateProvinceBusinessStakeHolderField))
				.postalCode("2222")
				.token(List.of(tokenBankAccountField))
				.hyperwalletProgram(List.of(hyperwalletProgramAccountField))
				.bankBic("bankBic")
				.build();
		//@formatter:on
	}

}
