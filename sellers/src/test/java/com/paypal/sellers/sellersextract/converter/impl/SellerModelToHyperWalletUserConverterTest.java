package com.paypal.sellers.sellersextract.converter.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.sellers.infrastructure.configuration.SellersHyperwalletApiConfig;
import com.paypal.sellers.sellersextract.model.SellerBusinessType;
import com.paypal.sellers.sellersextract.model.SellerGovernmentIdType;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.model.SellerProfileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerModelToHyperWalletUserConverterTest {

	private static final String PROGRAM_TOKEN = "PROGRAM_TOKEN";

	private static final String CLIENT_USER_ID = "1025";

	private static final String FIRST_NAME = "John";

	private static final String SECOND_NAME = "Doe";

	private static final String ADDRES_LINE_ONE = "Elmo Street";

	private static final String ADDRESS_LINE_TWO = "Door 1";

	private static final String BUSINESS_NAME = "Super Business";

	private static final String CITY = "Wonder Town";

	private static final String COUNTRY = "USA";

	private static final String COUNTRY_OF_BIRTH = "ESP";

	private static final Date DATE_OF_BIRTH = DateUtil.convertToDate(LocalDate.of(1985, 9, 6), ZoneId.systemDefault());

	private static final String COUNTRY_OF_NATIONALITY = "FRA";

	private static final String DRIVERS_LICENSE = "489663020J";

	private static final String EMAIL = "mysuperstore@paypal.com";

	private static final String EMPLOYER_ID = "employerId";

	private static final String GOVERNMENT_ID = "JKL20";

	private static final String MOBILE_PHONE = "+34656201324";

	private static final String PHONE_NUMBER = "+34920124568";

	private static final String POSTAL_CODE = "ZIP2005";

	private static final String STATE_PROVINCE = "Ohio";

	private static final String PASSPORT_ID = "12346502KLM";

	private static final String USER_TOKEN = "2134654512574";

	private static final String COMPANY_NAME = "companyName";

	private static final String COMPANY_REGISTRATION_NUMBER = "businessRegistrationId";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String BUSINESS_REGISTRATION_STATE_PROVINCE = "businessRegistrationStateProvince";

	private static final String COMPANY_REGISTRATION_COUNTRY = "companyRegistrationCountry";

	@InjectMocks
	private SellerModelToHyperWalletUserConverter testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private SellersHyperwalletApiConfig sellersHyperwalletApiConfig;

	@Mock
	private Map<String, String> tokensMock;

	@BeforeEach
	void setUp() {
		when(sellerModelMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(sellersHyperwalletApiConfig.getUserStoreTokens()).thenReturn(tokensMock);
		when(tokensMock.get(HYPERWALLET_PROGRAM)).thenReturn(PROGRAM_TOKEN);
		when(sellerModelMock.getClientUserId()).thenReturn(CLIENT_USER_ID);
		when(sellerModelMock.getFirstName()).thenReturn(FIRST_NAME);
		when(sellerModelMock.getLastName()).thenReturn(SECOND_NAME);
		when(sellerModelMock.getAddressLine1()).thenReturn(ADDRES_LINE_ONE);
		when(sellerModelMock.getAddressLine2()).thenReturn(ADDRESS_LINE_TWO);
		when(sellerModelMock.getBusinessName()).thenReturn(BUSINESS_NAME);
		when(sellerModelMock.getBusinessType()).thenReturn(SellerBusinessType.PRIVATE_COMPANY);
		when(sellerModelMock.getCity()).thenReturn(CITY);
		when(sellerModelMock.getCountry()).thenReturn(COUNTRY);
		when(sellerModelMock.getCountryOfBirth()).thenReturn(COUNTRY_OF_BIRTH);
		when(sellerModelMock.getDateOfBirth()).thenReturn(DATE_OF_BIRTH);
		when(sellerModelMock.getCountryOfNationality()).thenReturn(COUNTRY_OF_NATIONALITY);
		when(sellerModelMock.getDriversLicenseId()).thenReturn(DRIVERS_LICENSE);
		when(sellerModelMock.getEmail()).thenReturn(EMAIL);
		when(sellerModelMock.getEmployerId()).thenReturn(EMPLOYER_ID);
		when(sellerModelMock.getGovernmentIdType()).thenReturn(SellerGovernmentIdType.NATIONAL_ID_CARD);
		when(sellerModelMock.getGovernmentId()).thenReturn(GOVERNMENT_ID);
		when(sellerModelMock.getPassportId()).thenReturn(PASSPORT_ID);
		when(sellerModelMock.getMobilePhone()).thenReturn(MOBILE_PHONE);
		when(sellerModelMock.getPhoneNumber()).thenReturn(PHONE_NUMBER);
		when(sellerModelMock.getPostalCode()).thenReturn(POSTAL_CODE);
		when(sellerModelMock.getStateProvince()).thenReturn(STATE_PROVINCE);
		when(sellerModelMock.getCompanyName()).thenReturn(COMPANY_NAME);
		when(sellerModelMock.getCompanyRegistrationNumber()).thenReturn(COMPANY_REGISTRATION_NUMBER);
		when(sellerModelMock.getToken()).thenReturn(USER_TOKEN);
		when(sellerModelMock.getBusinessRegistrationStateProvince()).thenReturn(BUSINESS_REGISTRATION_STATE_PROVINCE);
		when(sellerModelMock.getCompanyRegistrationCountry()).thenReturn(COMPANY_REGISTRATION_COUNTRY);
	}

	@Test
	void convert_shouldCreateAHyperWalletUserWithTheDetailsFromTheProfessionalSellerModelPassedAsParameter() {
		when(sellerModelMock.getProfileType()).thenReturn(SellerProfileType.BUSINESS);

		final HyperwalletUser result = testObj.convert(sellerModelMock);

		assertThat(result).hasFieldOrPropertyWithValue("clientUserId", CLIENT_USER_ID)
				.hasFieldOrPropertyWithValue("businessName", BUSINESS_NAME)
				.hasFieldOrPropertyWithValue("profileType", HyperwalletUser.ProfileType.BUSINESS)
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", SECOND_NAME)
				.hasFieldOrPropertyWithValue("dateOfBirth", DATE_OF_BIRTH)
				.hasFieldOrPropertyWithValue("countryOfBirth", COUNTRY_OF_BIRTH)
				.hasFieldOrPropertyWithValue("countryOfNationality", COUNTRY_OF_NATIONALITY)
				.hasFieldOrPropertyWithValue("phoneNumber", PHONE_NUMBER)
				.hasFieldOrPropertyWithValue("mobileNumber", MOBILE_PHONE).hasFieldOrPropertyWithValue("email", EMAIL)
				.hasFieldOrPropertyWithValue("governmentId", GOVERNMENT_ID)
				.hasFieldOrPropertyWithValue("passportId", PASSPORT_ID)
				.hasFieldOrPropertyWithValue("employerId", EMPLOYER_ID)
				.hasFieldOrPropertyWithValue("addressLine1", ADDRES_LINE_ONE)
				.hasFieldOrPropertyWithValue("addressLine2", ADDRESS_LINE_TWO).hasFieldOrPropertyWithValue("city", CITY)
				.hasFieldOrPropertyWithValue("stateProvince", STATE_PROVINCE)
				.hasFieldOrPropertyWithValue("country", COUNTRY).hasFieldOrPropertyWithValue("postalCode", POSTAL_CODE)
				.hasFieldOrPropertyWithValue("programToken", PROGRAM_TOKEN)
				.hasFieldOrPropertyWithValue("driversLicenseId", DRIVERS_LICENSE)
				.hasFieldOrPropertyWithValue("businessType", HyperwalletUser.BusinessType.PRIVATE_COMPANY)
				.hasFieldOrPropertyWithValue("governmentIdType", HyperwalletUser.GovernmentIdType.NATIONAL_ID_CARD)
				.hasFieldOrPropertyWithValue("businessOperatingName", COMPANY_NAME)
				.hasFieldOrPropertyWithValue("token", USER_TOKEN)
				.hasFieldOrPropertyWithValue("businessRegistrationCountry", COMPANY_REGISTRATION_COUNTRY)
				.hasFieldOrPropertyWithValue("businessRegistrationStateProvince", BUSINESS_REGISTRATION_STATE_PROVINCE);
	}

	@Test
	void convert_shouldCreateAHyperWalletUserWithTheDetailsFromTheIndividualSellerModelPassedAsParameter() {
		when(sellerModelMock.getProfileType()).thenReturn(SellerProfileType.INDIVIDUAL);

		final HyperwalletUser result = testObj.convert(sellerModelMock);
		assertThat(result.getClientUserId()).isEqualTo(CLIENT_USER_ID);
		assertThat(result.getBusinessName()).isEqualTo(BUSINESS_NAME);
		assertThat(result.getProfileType()).isEqualTo(HyperwalletUser.ProfileType.INDIVIDUAL);
		assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(result.getLastName()).isEqualTo(SECOND_NAME);
		assertThat(result.getDateOfBirth()).isEqualTo(DATE_OF_BIRTH);
		assertThat(result.getCountryOfBirth()).isEqualTo(COUNTRY_OF_BIRTH);
		assertThat(result.getCountryOfNationality()).isEqualTo(COUNTRY_OF_NATIONALITY);
		assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
		assertThat(result.getMobileNumber()).isEqualTo(MOBILE_PHONE);
		assertThat(result.getEmail()).isEqualTo(EMAIL);
		assertThat(result.getGovernmentId()).isEqualTo(GOVERNMENT_ID);
		assertThat(result.getPassportId()).isEqualTo(PASSPORT_ID);
		assertThat(result.getEmployerId()).isEqualTo(EMPLOYER_ID);
		assertThat(result.getAddressLine1()).isEqualTo(ADDRES_LINE_ONE);
		assertThat(result.getAddressLine2()).isEqualTo(ADDRESS_LINE_TWO);
		assertThat(result.getCity()).isEqualTo(CITY);
		assertThat(result.getStateProvince()).isEqualTo(STATE_PROVINCE);
		assertThat(result.getCountry()).isEqualTo(COUNTRY);
		assertThat(result.getPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(result.getProgramToken()).isEqualTo(PROGRAM_TOKEN);
		assertThat(result.getDriversLicenseId()).isEqualTo(DRIVERS_LICENSE);
		assertThat(result.getBusinessType()).isEqualTo(HyperwalletUser.BusinessType.PRIVATE_COMPANY);
		assertThat(result.getGovernmentIdType()).isEqualTo(HyperwalletUser.GovernmentIdType.NATIONAL_ID_CARD);
		assertThat(result.getToken()).isEqualTo(USER_TOKEN);
	}

}
