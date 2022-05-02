package com.paypal.sellers.bankaccountextract.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.sellers.sellersextract.model.SellerModelConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BankAccountModelTest {

	private static final String TOKEN_VALUE_1 = "token1";

	private static final String TOKEN_VALUE_2 = "token2";

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {
		final BankAccountModel bankAccountModelOne = createBankAccountModelObject(TOKEN_VALUE_1);
		final BankAccountModel bankAccountModelTwo = createBankAccountModelObject(TOKEN_VALUE_1);

		final boolean result = bankAccountModelOne.equals(bankAccountModelTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {
		final BankAccountModel bankAccountModelOne = createBankAccountModelObject(TOKEN_VALUE_1);
		final BankAccountModel bankAccountModelTwo = createAnotherBankAccountModelObject();

		final boolean result = bankAccountModelOne.equals(bankAccountModelTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {
		final BankAccountModel bankAccountModelOne = createBankAccountModelObject(TOKEN_VALUE_1);

		final boolean result = bankAccountModelOne.equals(bankAccountModelOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {
		final BankAccountModel bankAccountModelOne = createBankAccountModelObject(TOKEN_VALUE_1);

		final Object o = new Object();

		final boolean result = bankAccountModelOne.equals(o);

		assertThat(result).isFalse();
	}

	@Test
	void toBuild_ShouldReturnCopyOfBankAccountModel() {

		final BankAccountModel bankAccountModel = createBankAccountModelObject(TOKEN_VALUE_1);
		final BankAccountModel copyBankAccountModel = bankAccountModel.toBuilder().build();

		assertThat(bankAccountModel).isEqualTo(copyBankAccountModel);
	}

	@Test
	void toBuild_ShouldReturnCopyOfBankAccountModelWithTheUpdatedToken() {

		final BankAccountModel bankAccountModel1 = createBankAccountModelObject(TOKEN_VALUE_1);
		final BankAccountModel bankAccountModel2 = createBankAccountModelObject(TOKEN_VALUE_2);
		final BankAccountModel copyBankAccountModel = bankAccountModel1.toBuilder().token(TOKEN_VALUE_2).build();

		assertThat(bankAccountModel2).isEqualTo(copyBankAccountModel);
	}

	private BankAccountModel createBankAccountModelObject(final String token) {
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
		return BankAccountModel.builder()
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
				.build();
		//@formatter:on
	}

	private BankAccountModel createAnotherBankAccountModelObject() {
		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue tokenBankAccountField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		tokenBankAccountField.setCode(HYPERWALLET_BANK_ACCOUNT_TOKEN);
		tokenBankAccountField.setValue("token");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue stateProvinceBusinessStakeHolderField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		stateProvinceBusinessStakeHolderField.setCode(HYPERWALLET_BANK_ACCOUNT_STATE);
		stateProvinceBusinessStakeHolderField.setValue("stateProvince");

		final MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue hyperwalletProgramAccountField = new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue();
		hyperwalletProgramAccountField.setCode(HYPERWALLET_PROGRAM);
		hyperwalletProgramAccountField.setValue("hyperwalletProgram");

		//@formatter:off
		return BankAccountModel.builder()
				.transferMethodCountry("USA")
				.transferMethodCurrency("USD")
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
				.build();
		//@formatter:on
	}

}
