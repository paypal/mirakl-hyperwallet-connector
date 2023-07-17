package com.paypal.kyc.incomingnotifications.model;

import com.paypal.kyc.documentextractioncommons.model.KYCDocumentCategoryEnum;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class KYCDocumentCategoryEnumTest {

	@ParameterizedTest
	@MethodSource("fieldNameAndCategory")
	void getDocumentCategoryForField_shouldReturnCategoryBasedOnFieldName(final String fieldName,
			final KYCDocumentCategoryEnum categoryExpected) {
		final KYCDocumentCategoryEnum result = KYCDocumentCategoryEnum.getDocumentCategoryForField(fieldName);

		assertThat(result).isEqualTo(categoryExpected);
	}

	private static Stream<Arguments> fieldNameAndCategory() {
		return Stream.of(Arguments.of("hw-ind-proof-address", KYCDocumentCategoryEnum.ADDRESS),
				Arguments.of("hw-prof-proof-business-front", KYCDocumentCategoryEnum.BUSINESS),
				Arguments.of("hw-bsh-letter-authorization", KYCDocumentCategoryEnum.AUTHORIZATION),
				Arguments.of("hw-bsh1-proof-identity-front", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("hw-bsh2-proof-identity-front", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("hw-bsh3-proof-identity-front", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("hw-bsh4-proof-identity-front", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("hw-bsh5-proof-identity-front", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("hw-bsh1-proof-identity-back", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("hw-bsh2-proof-identity-back", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("hw-bsh3-proof-identity-back", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("hw-bsh4-proof-identity-back", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("hw-bsh5-proof-identity-back", KYCDocumentCategoryEnum.IDENTIFICATION),
				Arguments.of("different_field", KYCDocumentCategoryEnum.UNKNOWN));
	}

}
