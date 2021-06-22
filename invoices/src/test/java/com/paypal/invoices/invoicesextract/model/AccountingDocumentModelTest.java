package com.paypal.invoices.invoicesextract.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountingDocumentModelTest {

	public static Stream<Arguments> provideValuesForTextAreaCustomFieldValues() {
		return Stream
				.of(Arguments.of(AccountingDocumentModel.builder().destinationToken(List.of(
						populateStringCustomFieldValue("hw-bankaccount-token", "destinationTokenValue"))),
						"destinationToken", "destinationTokenValue"),
						Arguments.of(
								AccountingDocumentModel.builder()
										.hyperwalletProgram(List.of(populateMiraklValueListCustomFieldValue(
												"hw-program", "hyperwalletProgramValue"))),
								"hyperwalletProgram", "hyperwalletProgramValue"),
						Arguments.of(AccountingDocumentModel.builder().destinationToken(Collections.emptyList()),
								"destinationToken", null));
	}

	private static MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue populateStringCustomFieldValue(
			final String code, final String value) {
		final var miraklStringAdditionalFieldValue = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		miraklStringAdditionalFieldValue.setCode(code);
		miraklStringAdditionalFieldValue.setValue(value);
		return miraklStringAdditionalFieldValue;
	}

	private static MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue populateMiraklValueListCustomFieldValue(
			final String code, final String value) {
		final var miraklValueListAdditionalFieldValue = new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue();
		miraklValueListAdditionalFieldValue.setCode(code);
		miraklValueListAdditionalFieldValue.setValue(value);
		return miraklValueListAdditionalFieldValue;
	}

	@ParameterizedTest
	@MethodSource("provideValuesForTextAreaCustomFieldValues")
	void destinationToken_ShouldSetStringValuesFromTextAreaCustomFieldValues(
			final AccountingDocumentModel.Builder invoiceModelBuilder, final String property,
			final String expectedValue) {
		final var result = invoiceModelBuilder.build();

		assertThat(result).hasFieldOrPropertyWithValue(property, expectedValue);
	}

	@Test
	void destinationToken_shouldSetAttributeValueInPlainText() {
		final var result = AccountingDocumentModel.builder().destinationToken("destinationTokenValue");

		assertThat(result).hasFieldOrPropertyWithValue("destinationToken", "destinationTokenValue");
	}

	@Test
	void hyperwalletProgram_shouldSetAttributeValueInPlainText() {
		final var result = AccountingDocumentModel.builder().hyperwalletProgram("hyperwalletProgramValue");

		assertThat(result).hasFieldOrPropertyWithValue("hyperwalletProgram", "hyperwalletProgramValue");
	}

}
