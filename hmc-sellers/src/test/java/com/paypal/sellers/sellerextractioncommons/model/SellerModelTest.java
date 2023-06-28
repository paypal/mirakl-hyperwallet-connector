package com.paypal.sellers.sellerextractioncommons.model;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.sellers.sellerextractioncommons.model.SellerBusinessType;
import com.paypal.sellers.sellerextractioncommons.model.SellerGovernmentIdType;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerProfileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants.HYPERWALLET_TERMS_CONSENT;
import static com.paypal.sellers.sellerextractioncommons.model.SellerModelConstants.HYPERWALLET_USER_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SellerModelTest {

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@Test
	void setCountry_shouldConvertTo2LettersWhenCountry3IsocodeExists() {
		//@formatter:off
		final SellerModel testObj = SellerModel.builder()
				.country("USA")
				.profileType(SellerProfileType.INDIVIDUAL)
				.build();
		//@formatter:on
		assertThat(testObj.getCountry()).isEqualTo("US");
	}

	@SuppressWarnings("java:S5778")
	@Test
	void setCountry_shouldThrowAnExceptionWhenCountry3IsocodeDoesNotExists() {
		//@formatter:off
		assertThatThrownBy(() -> SellerModel.builder()
				.country("PAY")
				.profileType(SellerProfileType.INDIVIDUAL)
				.build())
				.isInstanceOf(IllegalStateException.class).hasMessage("Country with isocode: [PAY] not valid");
		//@formatter:on
	}

	@ParameterizedTest
	@MethodSource
	void dateOfBirth_whenMiraklDateOfBirthCustomFieldValueHasAValue_shouldSetDateOfBirth(final String dateOfBirth,
			final String timeZone, final int expectedDayOfMonth) {
		final MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue dateOfBirthMiraklCustomField = new MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue();
		dateOfBirthMiraklCustomField.setCode("hw-date-of-birth");
		dateOfBirthMiraklCustomField.setValue(dateOfBirth);
		//@formatter:off
		final SellerModel result = SellerModel.builder().timeZone((timeZone))
				.dateOfBirth(List.of(dateOfBirthMiraklCustomField))
				.profileType(SellerProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		assertThat(result.getDateOfBirth()).hasYear(2020).hasMonth(10).hasDayOfMonth(expectedDayOfMonth);
	}

	private static Stream<Arguments> dateOfBirth_whenMiraklDateOfBirthCustomFieldValueHasAValue_shouldSetDateOfBirth() {
		return Stream.of(Arguments.of("2020-10-29T22:00:00Z", "UTC+2", 30),
				Arguments.of("2020-10-29T22:00:00Z", "UTC-1", 29), Arguments.of("2020-10-29T13:34:35Z", "UTC+2", 29));
	}

	@Test
	void dateOfBirth_whenMiraklDateOfBirthCustomFieldValueHasNoValue_shouldNotSetDateOfBirth() {
		//@formatter:off
		final SellerModel result = SellerModel.builder()
				.dateOfBirth(Collections.emptyList())
				.profileType(SellerProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		assertThat(result.getDateOfBirth()).isNull();
	}

	@Test
	void hwTermsConsent_whenMiraklHyperWalletTermsConditionsIsNull_shouldSethWTermsConsentToFalse() {
		//@formatter:off
		final SellerModel result = SellerModel.builder()
				.hwTermsConsent(Collections.emptyList())
				.profileType(SellerProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		assertThat(result.isHwTermsConsent()).isFalse();
	}

	@Test
	void hwTermsConsent_whenMiraklHyperWalletTermsConditionsHasAccepted_shouldSethWTermsConsentToTrue() {
		final MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue hwTermsConsentMiraklCustomField = new MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue();
		hwTermsConsentMiraklCustomField.setCode("hw-terms-consent");
		hwTermsConsentMiraklCustomField.setValue("true");
		//@formatter:off
		final SellerModel result = SellerModel.builder()
				.hwTermsConsent(List.of(hwTermsConsentMiraklCustomField))
				.profileType(SellerProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		assertThat(result.isHwTermsConsent()).isTrue();
	}

	@Test
	void dateOfBirth_whenMiraklDateOfBirthCustomFieldValueHasInvalidDateFormat_shouldNotSetDateOfBirth() {
		final MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue dateOfBirthMiraklCustomField = new MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue();
		dateOfBirthMiraklCustomField.setCode("date-of-birth");
		dateOfBirthMiraklCustomField.setValue("InvalidDateFormat");
		//@formatter:off
		final SellerModel result = SellerModel.builder()
				.dateOfBirth(List.of(dateOfBirthMiraklCustomField))
				.profileType(SellerProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		assertThat(result.getDateOfBirth()).isNull();
	}

	@ParameterizedTest
	@MethodSource("provideValuesForTextAreaCustomFieldValues")
	void test_ShouldSetStringValuesFromTextAreaCustomFieldValues(
			final SellerModel.SellerModelBuilder sellerModelBuilder, final String property,
			final String expectedValue) {
		//@formatter:off
		final SellerModel result = sellerModelBuilder
				.profileType(SellerProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		assertThat(result).hasFieldOrPropertyWithValue(property, expectedValue);
	}

	@ParameterizedTest
	@MethodSource("provideValuesForSingleValueCustomFieldValues")
	void test_ShouldSetStringValuesFromSingleValueListCustomFieldValues(
			final SellerModel.SellerModelBuilder sellerModelBuilder, final String property, final Enum expectedValue) {
		//@formatter:off
		final SellerModel result = sellerModelBuilder
				.profileType(SellerProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		assertThat(result).hasFieldOrPropertyWithValue(property, expectedValue);
	}

	@ParameterizedTest
	@MethodSource("termsAndConditionsTokenValues")
	void hasAcceptedTermsAndConditions_shouldReturnTrueWhenTermsAndConditionsOrUserTokenIsSet(
			final boolean termsAndConditions, final String userToken, final boolean expectedResult) {
		final SellerModel sellerModel = SellerModel.builder().profileType(SellerProfileType.INDIVIDUAL)
				.token(List.of(populateTextAreaCustomFieldValue(HYPERWALLET_USER_TOKEN, userToken)))
				.hwTermsConsent(List.of(populateBooleanAdditionalFieldValues(HYPERWALLET_TERMS_CONSENT,
						String.valueOf(expectedResult))))
				.build();

		assertThat(sellerModel.hasAcceptedTermsAndConditions()).isEqualTo(expectedResult);
	}

	@Test
	void toBuilder_shouldReturnCopyOfSellerModel() {
		final MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue dateOfBirthMiraklCustomField = new MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue();
		dateOfBirthMiraklCustomField.setCode("hw-date-of-birth");
		dateOfBirthMiraklCustomField.setValue("2020-10-29T13:34:35Z");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue countryOfBirthMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		countryOfBirthMiraklCustomField.setCode("hw-country-of-birth");
		countryOfBirthMiraklCustomField.setValue("USA");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue countryOfNationalityMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		countryOfNationalityMiraklCustomField.setCode("hw-country-of-nationality");
		countryOfNationalityMiraklCustomField.setValue("USA");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue governmentIdMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		governmentIdMiraklCustomField.setCode("hw-government-id");
		governmentIdMiraklCustomField.setValue("governmentId");

		final MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue governmentIdTypeMiraklCustomField = new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue();
		governmentIdTypeMiraklCustomField.setCode("hw-government-id-type");
		governmentIdTypeMiraklCustomField.setValue("PASSPORT");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue passwordMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		passwordMiraklCustomField.setCode("hw-passport-id");
		passwordMiraklCustomField.setValue("passportId");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue driversLisenceMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		driversLisenceMiraklCustomField.setCode("hw-drivers-license-id");
		driversLisenceMiraklCustomField.setValue("driversLicenseId");

		final MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue businessTypeMiraklCustomField = new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue();
		businessTypeMiraklCustomField.setCode("hw-business-type");
		businessTypeMiraklCustomField.setValue("CORPORATION");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue businessRegistrationStateProvinceMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		businessRegistrationStateProvinceMiraklCustomField.setCode("hw-business-reg-state-province");
		businessRegistrationStateProvinceMiraklCustomField.setValue("stateProvince");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue businessRegistrationCountryMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		businessRegistrationCountryMiraklCustomField.setCode("hw-business-reg-country");
		businessRegistrationCountryMiraklCustomField.setValue("US");

		final SellerModel testObj = SellerModel.builder().build();

		//@formatter:off
		final SellerModel result = testObj.toBuilder().clientUserId("clientUserId")
				.firstName("firstName")
				.lastName("lastName")
				.timeZone("UTC")
				.dateOfBirth(List.of(dateOfBirthMiraklCustomField))
				.countryOfBirth(List.of(countryOfBirthMiraklCustomField))
				.countryOfNationality(List.of(countryOfNationalityMiraklCustomField))
				.gender("MALE")
				.phoneNumber("phoneNumber")
				.mobilePhone("mobilePhone")
				.email("email")
				.governmentId(List.of(governmentIdMiraklCustomField))
				.governmentIdType(List.of(governmentIdTypeMiraklCustomField))
				.passportId(List.of(passwordMiraklCustomField))
				.driversLicenseId(List.of(driversLisenceMiraklCustomField))
				.addressLine1("addressLine1")
				.addressLine2("addressLine2")
				.city("city")
				.stateProvince("stateProvince")
				.country("USA")
				.postalCode("postalCode")
				.language("language")
				.programToken("programToken")
				.businessType(List.of(businessTypeMiraklCustomField))
				.businessName("businessName").token("token")
				.profileType(SellerProfileType.INDIVIDUAL)
				.companyName("companyName")
				.companyRegistrationNumber("companyRegistrationNumber")
				.vatNumber("vatNumber")
				.businessRegistrationStateProvince(List.of(businessRegistrationStateProvinceMiraklCustomField))
				.companyRegistrationCountry(List.of(businessRegistrationCountryMiraklCustomField))
				.build();

		assertThat(result.getDateOfBirth()).hasYear(2020).hasMonth(10).hasDayOfMonth(29);
		assertThat(result).hasFieldOrPropertyWithValue("clientUserId", "clientUserId")
				.hasFieldOrPropertyWithValue("firstName", "firstName")
				.hasFieldOrPropertyWithValue("lastName", "lastName")
				.hasFieldOrPropertyWithValue("countryOfBirth", "USA")
				.hasFieldOrPropertyWithValue("countryOfNationality", "USA")
				.hasFieldOrPropertyWithValue("gender", "MALE")
				.hasFieldOrPropertyWithValue("phoneNumber", "phoneNumber")
				.hasFieldOrPropertyWithValue("mobilePhone", "mobilePhone")
				.hasFieldOrPropertyWithValue("email", "email")
				.hasFieldOrPropertyWithValue("governmentId", "governmentId")
				.hasFieldOrPropertyWithValue("governmentIdType", SellerGovernmentIdType.PASSPORT)
				.hasFieldOrPropertyWithValue("passportId", "passportId")
				.hasFieldOrPropertyWithValue("driversLicenseId", "driversLicenseId")
				.hasFieldOrPropertyWithValue("addressLine1", "addressLine1")
				.hasFieldOrPropertyWithValue("addressLine2", "addressLine2")
				.hasFieldOrPropertyWithValue("city", "city")
				.hasFieldOrPropertyWithValue("stateProvince", "stateProvince")
				.hasFieldOrPropertyWithValue("country", "US")
				.hasFieldOrPropertyWithValue("postalCode", "postalCode")
				.hasFieldOrPropertyWithValue("language", "language")
				.hasFieldOrPropertyWithValue("programToken", "programToken")
				.hasFieldOrPropertyWithValue("businessType", SellerBusinessType.CORPORATION)
				.hasFieldOrPropertyWithValue("profileType", SellerProfileType.INDIVIDUAL)
				.hasFieldOrPropertyWithValue("companyName", "companyName")
				.hasFieldOrPropertyWithValue("companyRegistrationNumber", "companyRegistrationNumber")
				.hasFieldOrPropertyWithValue("vatNumber", "vatNumber")
				.hasFieldOrPropertyWithValue("companyRegistrationCountry", "US")
				.hasFieldOrPropertyWithValue("businessRegistrationStateProvince", "stateProvince");
		//@formatter:on
	}

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {
		final SellerModel sellerModelOne = createSellerModelObject();
		final SellerModel sellerModelTwo = sellerModelOne.toBuilder().build();

		final boolean result = sellerModelOne.equals(sellerModelTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {
		final SellerModel sellerModelOne = createSellerModelObject();
		final SellerModel sellerModelTwo = sellerModelOne.toBuilder().token("tokenNew").build();

		final boolean result = sellerModelOne.equals(sellerModelTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {
		final SellerModel sellerModelOne = createSellerModelObject();

		final boolean result = sellerModelOne.equals(sellerModelOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {
		final SellerModel sellerModelOne = createSellerModelObject();

		final Object o = new Object();

		final boolean result = sellerModelOne.equals(o);

		assertThat(result).isFalse();
	}

	private static Stream<Arguments> termsAndConditionsTokenValues() {
		return Stream.of(Arguments.of(Boolean.TRUE, "token", true), Arguments.of(Boolean.TRUE, null, true),
				Arguments.of(Boolean.FALSE, null, false), Arguments.of(Boolean.FALSE, "token", true));
	}

	private static Stream<Arguments> provideValuesForSingleValueCustomFieldValues() {
		return Stream.of(
				Arguments.of(
						SellerModel.builder().governmentIdType(
								List.of(populateSingleValueListCustomFieldValue("hw-government-id-type", "PASSPORT"))),
						"governmentIdType", SellerGovernmentIdType.PASSPORT),
				Arguments.of(
						SellerModel.builder()
								.governmentIdType(List.of(populateSingleValueListCustomFieldValue(
										"hw-government-id-type", "NATIONAL_ID_CARD"))),
						"governmentIdType", SellerGovernmentIdType.NATIONAL_ID_CARD),
				Arguments.of(
						SellerModel.builder().governmentIdType(
								List.of(populateSingleValueListCustomFieldValue("hw-government-id-type", "INVALID"))),
						"governmentIdType", null),
				Arguments.of(
						SellerModel
								.builder().businessType(List
										.of(populateSingleValueListCustomFieldValue("hw-business-type",
												"CORPORATION"))),
						"businessType", SellerBusinessType.CORPORATION),
				Arguments.of(SellerModel.builder().businessType(
						List.of(populateSingleValueListCustomFieldValue("hw-business-type", "GOVERNMENT_ENTITY"))),
						"businessType", SellerBusinessType.GOVERNMENT_ENTITY),
				Arguments.of(
						SellerModel.builder()
								.businessType(List.of(populateSingleValueListCustomFieldValue("hw-business-type",
										"NOT_FOR_PROFIT_ORGANIZATION"))),
						"businessType", SellerBusinessType.NOT_FOR_PROFIT_ORGANIZATION),
				Arguments.of(
						SellerModel.builder().businessType(
								List.of(populateSingleValueListCustomFieldValue("hw-business-type", "PARTNERSHIP"))),
						"businessType", SellerBusinessType.PARTNERSHIP),
				Arguments.of(SellerModel.builder().businessType(
						List.of(populateSingleValueListCustomFieldValue("hw-business-type", "PRIVATE_COMPANY"))),
						"businessType", SellerBusinessType.PRIVATE_COMPANY),
				Arguments.of(
						SellerModel.builder().businessType(
								List.of(populateSingleValueListCustomFieldValue("hw-business-type", "PUBLIC_COMPANY"))),
						"businessType", SellerBusinessType.PUBLIC_COMPANY),
				Arguments.of(
						SellerModel.builder().businessType(
								List.of(populateSingleValueListCustomFieldValue("hw-business-type", "INVALID"))),
						"businessType", null)

		);
	}

	private static Stream<Arguments> provideValuesForTextAreaCustomFieldValues() {
		return Stream.of(
				Arguments.of(
						SellerModel.builder().countryOfBirth(
								List.of(populateTextAreaCustomFieldValue("hw-country-of-birth", "Spain"))),
						"countryOfBirth", "Spain"),
				Arguments.of(SellerModel.builder().countryOfBirth(Collections.emptyList()), "countryOfBirth", null),
				Arguments.of(
						SellerModel.builder().countryOfNationality(
								List.of(populateTextAreaCustomFieldValue("hw-country-of-nationality", "France"))),
						"countryOfNationality", "France"),
				Arguments.of(SellerModel.builder().countryOfNationality(Collections.emptyList()),
						"countryOfNationality", null),
				Arguments.of(
						SellerModel.builder()
								.governmentId(List.of(populateTextAreaCustomFieldValue("hw-government-id", "12345"))),
						"governmentId", "12345"),
				Arguments.of(SellerModel.builder().governmentId(Collections.emptyList()), "governmentId", null),
				Arguments.of(
						SellerModel.builder()
								.passportId(List.of(populateTextAreaCustomFieldValue("hw-passport-id", "123456"))),
						"passportId", "123456"),
				Arguments.of(SellerModel.builder().passportId(Collections.emptyList()), "driversLicenseId", null),
				Arguments.of(
						SellerModel.builder().driversLicenseId(
								List.of(populateTextAreaCustomFieldValue("hw-drivers-license-id", "1234567"))),
						"driversLicenseId", "1234567"),
				Arguments.of(SellerModel.builder().driversLicenseId(Collections.emptyList()), "driversLicenseId", null),
				Arguments.of(
						SellerModel.builder().hyperwalletProgram(
								List.of(populateSingleValueListCustomFieldValue("hw-program", HYPERWALLET_PROGRAM))),
						"hyperwalletProgram", HYPERWALLET_PROGRAM));
	}

	private static MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue populateTextAreaCustomFieldValue(
			final String code, final String value) {
		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue miraklStringAdditionalFieldValue = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		miraklStringAdditionalFieldValue.setCode(code);
		miraklStringAdditionalFieldValue.setValue(value);
		return miraklStringAdditionalFieldValue;
	}

	private static MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue populateSingleValueListCustomFieldValue(
			final String code, final String value) {
		final MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue miraklValueListAdditionalFieldValue = new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue();
		miraklValueListAdditionalFieldValue.setCode(code);
		miraklValueListAdditionalFieldValue.setValue(value);
		return miraklValueListAdditionalFieldValue;
	}

	private static MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue populateBooleanAdditionalFieldValues(
			final String code, final String value) {
		final MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue miraklValueListAdditionalFieldValue = new MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue();
		miraklValueListAdditionalFieldValue.setCode(code);
		miraklValueListAdditionalFieldValue.setValue(value);
		return miraklValueListAdditionalFieldValue;
	}

	private SellerModel createSellerModelObject() {
		final MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue dateOfBirthMiraklCustomField = new MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue();
		dateOfBirthMiraklCustomField.setCode("hw-date-of-birth");
		dateOfBirthMiraklCustomField.setValue("2020-10-29T13:34:35Z");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue countryOfBirthMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		countryOfBirthMiraklCustomField.setCode("hw-country-of-birth");
		countryOfBirthMiraklCustomField.setValue("USA");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue countryOfNationalityMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		countryOfNationalityMiraklCustomField.setCode("hw-country-of-nationality");
		countryOfNationalityMiraklCustomField.setValue("USA");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue governmentIdMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		governmentIdMiraklCustomField.setCode("hw-government-id");
		governmentIdMiraklCustomField.setValue("governmentId");

		final MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue governmentIdTypeMiraklCustomField = new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue();
		governmentIdTypeMiraklCustomField.setCode("hw-government-id-type");
		governmentIdTypeMiraklCustomField.setValue("PASSPORT");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue passwordMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		passwordMiraklCustomField.setCode("hw-passport-id");
		passwordMiraklCustomField.setValue("passportId");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue driversLicenseMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		driversLicenseMiraklCustomField.setCode("hw-drivers-license-id");
		driversLicenseMiraklCustomField.setValue("driversLicenseId");

		final MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue businessTypeMiraklCustomField = new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue();
		businessTypeMiraklCustomField.setCode("hw-business-type");
		businessTypeMiraklCustomField.setValue("CORPORATION");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue businessRegistrationStateProvinceMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		businessRegistrationStateProvinceMiraklCustomField.setCode("hw-business-reg-state-province");
		businessRegistrationStateProvinceMiraklCustomField.setValue("stateProvince");

		final MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue businessRegistrationCountryMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		businessRegistrationCountryMiraklCustomField.setCode("hw-business-reg-country");
		businessRegistrationCountryMiraklCustomField.setValue("US");

		//@formatter:off
		return SellerModel.builder().clientUserId("clientUserId")
				.firstName("firstName")
				.lastName("lastName")
				.timeZone("UTC")
				.dateOfBirth(List.of(dateOfBirthMiraklCustomField))
				.countryOfBirth(List.of(countryOfBirthMiraklCustomField))
				.countryOfNationality(List.of(countryOfNationalityMiraklCustomField))
				.gender("MALE")
				.phoneNumber("phoneNumber")
				.mobilePhone("mobilePhone")
				.email("email")
				.governmentId(List.of(governmentIdMiraklCustomField))
				.governmentIdType(List.of(governmentIdTypeMiraklCustomField))
				.passportId(List.of(passwordMiraklCustomField))
				.driversLicenseId(List.of(driversLicenseMiraklCustomField))
				.addressLine1("addressLine1")
				.addressLine2("addressLine2")
				.city("city")
				.stateProvince("stateProvince")
				.country("USA")
				.postalCode("postalCode")
				.language("language")
				.programToken("programToken")
				.businessType(List.of(businessTypeMiraklCustomField))
				.businessName("businessName").token("token")
				.profileType(SellerProfileType.INDIVIDUAL)
				.companyName("companyName")
				.companyRegistrationNumber("companyRegistrationNumber")
				.vatNumber("vatNumber")
				.businessRegistrationStateProvince(List.of(businessRegistrationStateProvinceMiraklCustomField))
				.companyRegistrationCountry(List.of(businessRegistrationCountryMiraklCustomField))
				.build();
	}

}
