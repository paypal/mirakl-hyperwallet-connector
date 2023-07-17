package com.paypal.sellers.bankaccountextraction.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.sellers.bankaccountextraction.model.ABABankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.BankAccountType;
import com.paypal.sellers.bankaccountextraction.model.TransferType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ABABankAccountModelTest {

	private static final String TOKEN_VALUE_1 = "token1";

	private static final String TOKEN_VALUE_2 = "token2";

	@Test
	void setCountry_shouldConvertTo2LettersWhenCountry3IsocodeExists() {
		final ABABankAccountModel testObj = ABABankAccountModel.builder().country("USA").build();

		assertThat(testObj.getCountry()).isEqualTo("US");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setCountry_shouldThrowAnExceptionWhenCountry3IsocodeDoesNotExists() {
		assertThatThrownBy(() -> ABABankAccountModel.builder().country("PAY").build())
				.isInstanceOf(IllegalStateException.class).hasMessage("Country with isocode: [PAY] not valid");
	}

	@Test
	void setTransferMethodCountry_shouldConvertTo2LettersWhenCountry3IsocodeExists() {
		final ABABankAccountModel testObj = ABABankAccountModel.builder().transferMethodCountry("USA").build();

		assertThat(testObj.getTransferMethodCountry()).isEqualTo("US");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setTransferMethodCountry_shouldThrowAnExceptionWhenCountry3IsocodeDoesNotExists() {
		assertThatThrownBy(() -> ABABankAccountModel.builder().transferMethodCountry("PAY").build())
				.isInstanceOf(IllegalStateException.class).hasMessage("Country with isocode: [PAY] not valid");
	}

	@Test
	void setTransferMethodCurrency_shouldSetCurrencyIsoCodeWhenCurrencyIsoCodeIsValid() {
		final ABABankAccountModel testObj = ABABankAccountModel.builder().transferMethodCurrency("EUR").build();

		assertThat(testObj.getTransferMethodCurrency()).isEqualTo("EUR");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setTransferMethodCurrency_shouldThrowAnExceptionWhenCurrencyIsInvalid() {
		assertThatThrownBy(() -> ABABankAccountModel.builder().transferMethodCurrency("INVALID_CURRENCY").build())
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Transfer method currency with code: [INVALID_CURRENCY] not valid");
	}

	@Test
	void toBuild_ShouldReturnCopyOABABankAccountModel() {

		final ABABankAccountModel bankAccountModel = createABABankAccountModelObject(TOKEN_VALUE_1);
		final ABABankAccountModel copyBankAccountModel = bankAccountModel.toBuilder().build();

		assertThat(bankAccountModel).isEqualTo(copyBankAccountModel);
	}

	@Test
	void toBuild_ShouldReturnCopyOfABABankAccountModelWithTheUpdatedToken() {

		final ABABankAccountModel bankAccountModel1 = createABABankAccountModelObject(TOKEN_VALUE_1);
		final ABABankAccountModel bankAccountModel2 = createABABankAccountModelObject(TOKEN_VALUE_2);
		final ABABankAccountModel copyBankAccountModel = bankAccountModel1.toBuilder().token(TOKEN_VALUE_2).build();

		assertThat(bankAccountModel2).isEqualTo(copyBankAccountModel);
	}

	private ABABankAccountModel createABABankAccountModelObject(final String token) {
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
		return ABABankAccountModel.builder()
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
				.branchId("branchId")
				.bankAccountPurpose("bankAccountPurpose")
				.build();
		//@formatter:on
	}

}
