package com.paypal.sellers.sellersextract.converter.impl;

import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.model.HyperwalletUser.Gender;
import com.paypal.infrastructure.constants.HyperWalletConstants;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerGender;
import com.paypal.sellers.sellersextract.model.SellerGovernmentIdType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.TimeZone;

import static com.hyperwallet.clientsdk.model.HyperwalletUser.GovernmentIdType.NATIONAL_ID_CARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessStakeHolderModelToHyperWalletBusinessStakeHolderConverterTest {

	private static final String FIRST_NAME = "firstName";

	private static final String MIDDLE_NAME = "middleName";

	private static final String LAST_NAME = "lastName";

	private static final String DATE_OF_BIRTH = "1970-10-10T10:00:00";

	private static final String COUNTRY_OF_BIRTH = "countryOfBirth";

	private static final String COUNTRY_OF_NATIONALITY = "countryOfNationality";

	private static final String PHONE_NUMBER = "phoneNumber";

	private static final String MOBILE_NUMBER = "mobileNumber";

	private static final String EMAIL = "email";

	private static final String GOVERNMENT_ID = "governmentId";

	private static final String DRIVERS_LICENSE_ID = "driversLicenseId";

	private static final String ADDRESS_LINE_1 = "addressLine1";

	private static final String ADDRESS_LINE_2 = "addressLine2";

	private static final String CITY = "city";

	private static final String STATE_PROVINCE = "stateProvince";

	private static final String COUNTRY = "country";

	private static final String POSTAL_CODE = "postalCode";

	private static final String TOKEN = "TOKEN";

	@InjectMocks
	private BusinessStakeHolderModelToHyperWalletBusinessStakeHolderConverter testObj;

	@Mock
	private BusinessStakeHolderModel businessStakeHolderModelMock;

	@Test
	void convert_shouldReturnHyperWalletBusinessStakeHolder() {
		when(businessStakeHolderModelMock.getBusinessContact()).thenReturn(Boolean.TRUE);
		when(businessStakeHolderModelMock.getDirector()).thenReturn(Boolean.FALSE);
		when(businessStakeHolderModelMock.getUbo()).thenReturn(Boolean.FALSE);
		when(businessStakeHolderModelMock.getSmo()).thenReturn(Boolean.TRUE);
		when(businessStakeHolderModelMock.getFirstName()).thenReturn(FIRST_NAME);
		when(businessStakeHolderModelMock.getMiddleName()).thenReturn(MIDDLE_NAME);
		when(businessStakeHolderModelMock.getLastName()).thenReturn(LAST_NAME);
		when(businessStakeHolderModelMock.getDateOfBirth()).thenReturn(DateUtil.convertToDate(DATE_OF_BIRTH,
				HyperWalletConstants.HYPERWALLET_DATE_FORMAT, TimeZone.getTimeZone("UTC")));
		when(businessStakeHolderModelMock.getCountryOfBirth()).thenReturn(COUNTRY_OF_BIRTH);
		when(businessStakeHolderModelMock.getCountryOfNationality()).thenReturn(COUNTRY_OF_NATIONALITY);
		when(businessStakeHolderModelMock.getGender()).thenReturn(SellerGender.MALE);
		when(businessStakeHolderModelMock.getPhoneNumber()).thenReturn(PHONE_NUMBER);
		when(businessStakeHolderModelMock.getMobileNumber()).thenReturn(MOBILE_NUMBER);
		when(businessStakeHolderModelMock.getEmail()).thenReturn(EMAIL);
		when(businessStakeHolderModelMock.getGovernmentId()).thenReturn(GOVERNMENT_ID);
		when(businessStakeHolderModelMock.getGovernmentIdType()).thenReturn(SellerGovernmentIdType.NATIONAL_ID_CARD);
		when(businessStakeHolderModelMock.getDriversLicenseId()).thenReturn(DRIVERS_LICENSE_ID);
		when(businessStakeHolderModelMock.getAddressLine1()).thenReturn(ADDRESS_LINE_1);
		when(businessStakeHolderModelMock.getAddressLine2()).thenReturn(ADDRESS_LINE_2);
		when(businessStakeHolderModelMock.getCity()).thenReturn(CITY);
		when(businessStakeHolderModelMock.getStateProvince()).thenReturn(STATE_PROVINCE);
		when(businessStakeHolderModelMock.getCountry()).thenReturn(COUNTRY);
		when(businessStakeHolderModelMock.getPostalCode()).thenReturn(POSTAL_CODE);
		when(businessStakeHolderModelMock.getToken()).thenReturn(TOKEN);

		final HyperwalletBusinessStakeholder result = testObj.convert(businessStakeHolderModelMock);

		assertThat(result.getToken()).isEqualTo(TOKEN);
		assertThat(result.getIsBusinessContact()).isTrue();
		assertThat(result.getIsDirector()).isFalse();
		assertThat(result.getIsUltimateBeneficialOwner()).isFalse();
		assertThat(result.getIsSeniorManagingOfficial()).isTrue();
		assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(result.getMiddleName()).isEqualTo(MIDDLE_NAME);
		assertThat(result.getLastName()).isEqualTo(LAST_NAME);
		assertThat(result.getDateOfBirth()).isEqualTo(DateUtil.convertToDate(DATE_OF_BIRTH,
				HyperWalletConstants.HYPERWALLET_DATE_FORMAT, TimeZone.getTimeZone("UTC")));
		assertThat(result.getCountryOfBirth()).isEqualTo(COUNTRY_OF_BIRTH);
		assertThat(result.getCountryOfNationality()).isEqualTo(COUNTRY_OF_NATIONALITY);
		assertThat(result.getGender().name()).isEqualTo(Gender.MALE.name());
		assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
		assertThat(result.getMobileNumber()).isEqualTo(MOBILE_NUMBER);
		assertThat(result.getEmail()).isEqualTo(EMAIL);
		assertThat(result.getGovernmentId()).isEqualTo(GOVERNMENT_ID);
		assertThat(result.getGovernmentIdType().name()).isEqualTo(NATIONAL_ID_CARD.name());
		assertThat(result.getDriversLicenseId()).isEqualTo(DRIVERS_LICENSE_ID);
		assertThat(result.getAddressLine1()).isEqualTo(ADDRESS_LINE_1);
		assertThat(result.getAddressLine2()).isEqualTo(ADDRESS_LINE_2);
		assertThat(result.getCity()).isEqualTo(CITY);
		assertThat(result.getStateProvince()).isEqualTo(STATE_PROVINCE);
		assertThat(result.getCountry()).isEqualTo(COUNTRY);
		assertThat(result.getPostalCode()).isEqualTo(POSTAL_CODE);
	}

	@Test
	void convert_shouldReturnHyperWalletBusinessStakeHolder_whenStringValueIsNull() {
		when(businessStakeHolderModelMock.getFirstName()).thenReturn(null);
		when(businessStakeHolderModelMock.getBusinessContact()).thenReturn(Boolean.TRUE);
		when(businessStakeHolderModelMock.getDirector()).thenReturn(Boolean.FALSE);
		when(businessStakeHolderModelMock.getUbo()).thenReturn(Boolean.FALSE);
		when(businessStakeHolderModelMock.getSmo()).thenReturn(Boolean.TRUE);
		when(businessStakeHolderModelMock.getMiddleName()).thenReturn(MIDDLE_NAME);
		when(businessStakeHolderModelMock.getLastName()).thenReturn(LAST_NAME);
		when(businessStakeHolderModelMock.getDateOfBirth()).thenReturn(DateUtil.convertToDate(DATE_OF_BIRTH,
				HyperWalletConstants.HYPERWALLET_DATE_FORMAT, TimeZone.getTimeZone("UTC")));
		when(businessStakeHolderModelMock.getCountryOfBirth()).thenReturn(COUNTRY_OF_BIRTH);
		when(businessStakeHolderModelMock.getCountryOfNationality()).thenReturn(COUNTRY_OF_NATIONALITY);
		when(businessStakeHolderModelMock.getGender()).thenReturn(SellerGender.MALE);
		when(businessStakeHolderModelMock.getPhoneNumber()).thenReturn(PHONE_NUMBER);
		when(businessStakeHolderModelMock.getMobileNumber()).thenReturn(MOBILE_NUMBER);
		when(businessStakeHolderModelMock.getEmail()).thenReturn(EMAIL);
		when(businessStakeHolderModelMock.getGovernmentId()).thenReturn(GOVERNMENT_ID);
		when(businessStakeHolderModelMock.getGovernmentIdType()).thenReturn(SellerGovernmentIdType.NATIONAL_ID_CARD);
		when(businessStakeHolderModelMock.getDriversLicenseId()).thenReturn(DRIVERS_LICENSE_ID);
		when(businessStakeHolderModelMock.getAddressLine1()).thenReturn(ADDRESS_LINE_1);
		when(businessStakeHolderModelMock.getAddressLine2()).thenReturn(ADDRESS_LINE_2);
		when(businessStakeHolderModelMock.getCity()).thenReturn(CITY);
		when(businessStakeHolderModelMock.getStateProvince()).thenReturn(STATE_PROVINCE);
		when(businessStakeHolderModelMock.getCountry()).thenReturn(COUNTRY);
		when(businessStakeHolderModelMock.getPostalCode()).thenReturn(POSTAL_CODE);

		final HyperwalletBusinessStakeholder result = testObj.convert(businessStakeHolderModelMock);

		assertThat(result.getFirstName()).isNull();
		assertThat(result.getIsBusinessContact()).isTrue();
		assertThat(result.getIsDirector()).isFalse();
		assertThat(result.getIsUltimateBeneficialOwner()).isFalse();
		assertThat(result.getIsSeniorManagingOfficial()).isTrue();

		assertThat(result.getMiddleName()).isEqualTo(MIDDLE_NAME);
		assertThat(result.getLastName()).isEqualTo(LAST_NAME);
		assertThat(result.getDateOfBirth()).isEqualTo(DateUtil.convertToDate(DATE_OF_BIRTH,
				HyperWalletConstants.HYPERWALLET_DATE_FORMAT, TimeZone.getTimeZone("UTC")));
		assertThat(result.getCountryOfBirth()).isEqualTo(COUNTRY_OF_BIRTH);
		assertThat(result.getCountryOfNationality()).isEqualTo(COUNTRY_OF_NATIONALITY);
		assertThat(result.getGender().name()).isEqualTo(Gender.MALE.name());
		assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
		assertThat(result.getMobileNumber()).isEqualTo(MOBILE_NUMBER);
		assertThat(result.getEmail()).isEqualTo(EMAIL);
		assertThat(result.getGovernmentId()).isEqualTo(GOVERNMENT_ID);
		assertThat(result.getGovernmentIdType().name()).isEqualTo(NATIONAL_ID_CARD.name());
		assertThat(result.getDriversLicenseId()).isEqualTo(DRIVERS_LICENSE_ID);
		assertThat(result.getAddressLine1()).isEqualTo(ADDRESS_LINE_1);
		assertThat(result.getAddressLine2()).isEqualTo(ADDRESS_LINE_2);
		assertThat(result.getCity()).isEqualTo(CITY);
		assertThat(result.getStateProvince()).isEqualTo(STATE_PROVINCE);
		assertThat(result.getCountry()).isEqualTo(COUNTRY);
		assertThat(result.getPostalCode()).isEqualTo(POSTAL_CODE);
	}

	@Test
	void convert_shouldReturnHyperWalletBusinessStakeHolder_whenDateIsNull() {
		when(businessStakeHolderModelMock.getDateOfBirth()).thenReturn(null);
		when(businessStakeHolderModelMock.getBusinessContact()).thenReturn(Boolean.TRUE);
		when(businessStakeHolderModelMock.getDirector()).thenReturn(Boolean.FALSE);
		when(businessStakeHolderModelMock.getUbo()).thenReturn(Boolean.FALSE);
		when(businessStakeHolderModelMock.getSmo()).thenReturn(Boolean.TRUE);
		when(businessStakeHolderModelMock.getFirstName()).thenReturn(FIRST_NAME);
		when(businessStakeHolderModelMock.getMiddleName()).thenReturn(MIDDLE_NAME);
		when(businessStakeHolderModelMock.getLastName()).thenReturn(LAST_NAME);
		when(businessStakeHolderModelMock.getCountryOfBirth()).thenReturn(COUNTRY_OF_BIRTH);
		when(businessStakeHolderModelMock.getCountryOfNationality()).thenReturn(COUNTRY_OF_NATIONALITY);
		when(businessStakeHolderModelMock.getGender()).thenReturn(SellerGender.MALE);
		when(businessStakeHolderModelMock.getPhoneNumber()).thenReturn(PHONE_NUMBER);
		when(businessStakeHolderModelMock.getMobileNumber()).thenReturn(MOBILE_NUMBER);
		when(businessStakeHolderModelMock.getEmail()).thenReturn(EMAIL);
		when(businessStakeHolderModelMock.getGovernmentId()).thenReturn(GOVERNMENT_ID);
		when(businessStakeHolderModelMock.getGovernmentIdType()).thenReturn(SellerGovernmentIdType.NATIONAL_ID_CARD);
		when(businessStakeHolderModelMock.getDriversLicenseId()).thenReturn(DRIVERS_LICENSE_ID);
		when(businessStakeHolderModelMock.getAddressLine1()).thenReturn(ADDRESS_LINE_1);
		when(businessStakeHolderModelMock.getAddressLine2()).thenReturn(ADDRESS_LINE_2);
		when(businessStakeHolderModelMock.getCity()).thenReturn(CITY);
		when(businessStakeHolderModelMock.getStateProvince()).thenReturn(STATE_PROVINCE);
		when(businessStakeHolderModelMock.getCountry()).thenReturn(COUNTRY);
		when(businessStakeHolderModelMock.getPostalCode()).thenReturn(POSTAL_CODE);

		final HyperwalletBusinessStakeholder result = testObj.convert(businessStakeHolderModelMock);

		assertThat(result.getDateOfBirth()).isNull();
		assertThat(result.getIsBusinessContact()).isTrue();
		assertThat(result.getIsDirector()).isFalse();
		assertThat(result.getIsUltimateBeneficialOwner()).isFalse();
		assertThat(result.getIsSeniorManagingOfficial()).isTrue();
		assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(result.getMiddleName()).isEqualTo(MIDDLE_NAME);
		assertThat(result.getLastName()).isEqualTo(LAST_NAME);
		assertThat(result.getCountryOfBirth()).isEqualTo(COUNTRY_OF_BIRTH);
		assertThat(result.getCountryOfNationality()).isEqualTo(COUNTRY_OF_NATIONALITY);
		assertThat(result.getGender().name()).isEqualTo(Gender.MALE.name());
		assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
		assertThat(result.getMobileNumber()).isEqualTo(MOBILE_NUMBER);
		assertThat(result.getEmail()).isEqualTo(EMAIL);
		assertThat(result.getGovernmentId()).isEqualTo(GOVERNMENT_ID);
		assertThat(result.getGovernmentIdType().name()).isEqualTo(NATIONAL_ID_CARD.name());
		assertThat(result.getDriversLicenseId()).isEqualTo(DRIVERS_LICENSE_ID);
		assertThat(result.getAddressLine1()).isEqualTo(ADDRESS_LINE_1);
		assertThat(result.getAddressLine2()).isEqualTo(ADDRESS_LINE_2);
		assertThat(result.getCity()).isEqualTo(CITY);
		assertThat(result.getStateProvince()).isEqualTo(STATE_PROVINCE);
		assertThat(result.getCountry()).isEqualTo(COUNTRY);
		assertThat(result.getPostalCode()).isEqualTo(POSTAL_CODE);
	}

	@Test
	void convert_shouldReturnHyperWalletBusinessStakeHolder_whenAnEnumIsNull() {
		when(businessStakeHolderModelMock.getGender()).thenReturn(null);
		when(businessStakeHolderModelMock.getBusinessContact()).thenReturn(Boolean.TRUE);
		when(businessStakeHolderModelMock.getDirector()).thenReturn(Boolean.FALSE);
		when(businessStakeHolderModelMock.getUbo()).thenReturn(Boolean.FALSE);
		when(businessStakeHolderModelMock.getSmo()).thenReturn(Boolean.TRUE);
		when(businessStakeHolderModelMock.getFirstName()).thenReturn(FIRST_NAME);
		when(businessStakeHolderModelMock.getMiddleName()).thenReturn(MIDDLE_NAME);
		when(businessStakeHolderModelMock.getLastName()).thenReturn(LAST_NAME);
		when(businessStakeHolderModelMock.getDateOfBirth()).thenReturn(DateUtil.convertToDate(DATE_OF_BIRTH,
				HyperWalletConstants.HYPERWALLET_DATE_FORMAT, TimeZone.getTimeZone("UTC")));
		when(businessStakeHolderModelMock.getCountryOfBirth()).thenReturn(COUNTRY_OF_BIRTH);
		when(businessStakeHolderModelMock.getCountryOfNationality()).thenReturn(COUNTRY_OF_NATIONALITY);
		when(businessStakeHolderModelMock.getPhoneNumber()).thenReturn(PHONE_NUMBER);
		when(businessStakeHolderModelMock.getMobileNumber()).thenReturn(MOBILE_NUMBER);
		when(businessStakeHolderModelMock.getEmail()).thenReturn(EMAIL);
		when(businessStakeHolderModelMock.getGovernmentId()).thenReturn(GOVERNMENT_ID);
		when(businessStakeHolderModelMock.getGovernmentIdType()).thenReturn(SellerGovernmentIdType.NATIONAL_ID_CARD);
		when(businessStakeHolderModelMock.getDriversLicenseId()).thenReturn(DRIVERS_LICENSE_ID);
		when(businessStakeHolderModelMock.getAddressLine1()).thenReturn(ADDRESS_LINE_1);
		when(businessStakeHolderModelMock.getAddressLine2()).thenReturn(ADDRESS_LINE_2);
		when(businessStakeHolderModelMock.getCity()).thenReturn(CITY);
		when(businessStakeHolderModelMock.getStateProvince()).thenReturn(STATE_PROVINCE);
		when(businessStakeHolderModelMock.getCountry()).thenReturn(COUNTRY);
		when(businessStakeHolderModelMock.getPostalCode()).thenReturn(POSTAL_CODE);

		final HyperwalletBusinessStakeholder result = testObj.convert(businessStakeHolderModelMock);

		assertThat(result.getGender()).isNull();
		assertThat(result.getIsBusinessContact()).isTrue();
		assertThat(result.getIsDirector()).isFalse();
		assertThat(result.getIsUltimateBeneficialOwner()).isFalse();
		assertThat(result.getIsSeniorManagingOfficial()).isTrue();
		assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(result.getMiddleName()).isEqualTo(MIDDLE_NAME);
		assertThat(result.getLastName()).isEqualTo(LAST_NAME);
		assertThat(result.getDateOfBirth()).isEqualTo(DateUtil.convertToDate(DATE_OF_BIRTH,
				HyperWalletConstants.HYPERWALLET_DATE_FORMAT, TimeZone.getTimeZone("UTC")));
		assertThat(result.getCountryOfBirth()).isEqualTo(COUNTRY_OF_BIRTH);
		assertThat(result.getCountryOfNationality()).isEqualTo(COUNTRY_OF_NATIONALITY);
		assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
		assertThat(result.getMobileNumber()).isEqualTo(MOBILE_NUMBER);
		assertThat(result.getEmail()).isEqualTo(EMAIL);
		assertThat(result.getGovernmentId()).isEqualTo(GOVERNMENT_ID);
		assertThat(result.getGovernmentIdType().name()).isEqualTo(NATIONAL_ID_CARD.name());
		assertThat(result.getDriversLicenseId()).isEqualTo(DRIVERS_LICENSE_ID);
		assertThat(result.getAddressLine1()).isEqualTo(ADDRESS_LINE_1);
		assertThat(result.getAddressLine2()).isEqualTo(ADDRESS_LINE_2);
		assertThat(result.getCity()).isEqualTo(CITY);
		assertThat(result.getStateProvince()).isEqualTo(STATE_PROVINCE);
		assertThat(result.getCountry()).isEqualTo(COUNTRY);
		assertThat(result.getPostalCode()).isEqualTo(POSTAL_CODE);
	}

}
