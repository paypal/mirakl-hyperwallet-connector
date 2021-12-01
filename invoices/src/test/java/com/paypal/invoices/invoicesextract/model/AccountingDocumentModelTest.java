package com.paypal.invoices.invoicesextract.model;

import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel.Builder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue;
import static com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue;
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

	private static MiraklStringAdditionalFieldValue populateStringCustomFieldValue(final String code,
			final String value) {
		final MiraklStringAdditionalFieldValue miraklStringAdditionalFieldValue = new MiraklStringAdditionalFieldValue();
		miraklStringAdditionalFieldValue.setCode(code);
		miraklStringAdditionalFieldValue.setValue(value);
		return miraklStringAdditionalFieldValue;
	}

	private static MiraklValueListAdditionalFieldValue populateMiraklValueListCustomFieldValue(final String code,
			final String value) {
		final MiraklValueListAdditionalFieldValue miraklValueListAdditionalFieldValue = new MiraklValueListAdditionalFieldValue();
		miraklValueListAdditionalFieldValue.setCode(code);
		miraklValueListAdditionalFieldValue.setValue(value);
		return miraklValueListAdditionalFieldValue;
	}

	@ParameterizedTest
	@MethodSource("provideValuesForTextAreaCustomFieldValues")
	void destinationToken_ShouldSetStringValuesFromTextAreaCustomFieldValues(final Builder invoiceModelBuilder,
			final String property, final String expectedValue) {
		final AccountingDocumentModel result = invoiceModelBuilder.build();

		assertThat(result).hasFieldOrPropertyWithValue(property, expectedValue);
	}

	@Test
	void destinationToken_shouldSetAttributeValueInPlainText() {
		final Builder result = AccountingDocumentModel.builder().destinationToken("destinationTokenValue");

		assertThat(result).hasFieldOrPropertyWithValue("destinationToken", "destinationTokenValue");
	}

	@Test
	void hyperwalletProgram_shouldSetAttributeValueInPlainText() {
		final Builder result = AccountingDocumentModel.builder().hyperwalletProgram("hyperwalletProgramValue");

		assertThat(result).hasFieldOrPropertyWithValue("hyperwalletProgram", "hyperwalletProgramValue");
	}

}
