package com.paypal.sellers.professionalsellersextraction.services.converters;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.billing.MiraklDefaultBillingInformation;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.sellerextractioncommons.configuration.SellersMiraklApiConfig;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerProfileType;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import com.paypal.sellers.utils.LanguageConverter;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklShopToProfessionalSellerModelConverterTest {

	private static final String CORP_NAME = "Globex Corporation";

	private static final String IDENTIFICATION_NUMBER = "123478532";

	private static final String VAT_NUMBER = "vatNumber";

	private static final String CLIENT_ID_1 = "clientID1";

	private static final String STATE_PROVINCE_VALUE = "stateProvince";

	private static final String COUNTRIES_NOT_LOCAL_TAX = "countriesNotLocalTax";

	private static final String LOCAL_VAT_NUMBER = "localTaxNumber";

	@Spy
	@InjectMocks
	private MyMiraklShopToProfessionalSellerModelConverter testObj;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private Converter<Triple<List<MiraklAdditionalFieldValue>, Integer, String>, BusinessStakeHolderModel> businessStakeHolderModelConverterMock;

	@Mock
	private MiraklDefaultBillingInformation miraklDefaultBillingInformationMock;

	@Mock
	private MiraklDefaultBillingInformation.CorporateInformation miraklCorporateInformationMock;

	@Mock
	private MiraklDefaultBillingInformation.FiscalInformation miraklFiscalInformationMock;

	@Mock
	private MiraklAdditionalFieldValue tokenOneAdditionalFieldMock, businessOneAdditionalFieldMock,
			directorOneAdditionalFieldMock, uboOneAdditionalFieldMock, smoOneAdditionalFieldMock,
			firstNameOneAdditionalFieldMock, middleNameOneAdditionalFieldMock, lastNameOneAdditionalFieldMock,
			dobOneAdditionalFieldMock, countryOfBirthOneAdditionalFieldMock, nationalityOneAdditionalFieldMock,
			genderOneAdditionalFieldMock, phoneNumberOneAdditionalFieldMock, mobileNumberOneAdditionalFieldMock,
			emailOneAdditionalFieldMock, addressLine1OneAdditionalFieldMock, addressLine2OneAdditionalFieldMock,
			cityOneAdditionalFieldMock, stateOneAdditionalFieldMock, postCodeOneAdditionalFieldMock,
			countryOneAdditionalFieldMock, governmentIdTypeOneAdditionalFieldMock,
			governmentIdNumOneAdditionalFieldMock, governmentIdCountOneAdditionalFieldMock,
			driversLicenseOneNumAdditionalFieldMock, driversLicenseCntOneAdditionalFieldMock;

	@Mock
	private MiraklAdditionalFieldValue tokenTwoAdditionalFieldMock, businessTwoAdditionalFieldMock,
			directorTwoAdditionalFieldMock, uboTwoAdditionalFieldMock, smoTwoAdditionalFieldMock,
			firstNameTwoAdditionalFieldMock, middleNameTwoAdditionalFieldMock, lastNameTwoAdditionalFieldMock,
			dobTwoAdditionalFieldMock, countryOfBirthTwoAdditionalFieldMock, nationalityTwoAdditionalFieldMock,
			genderTwoAdditionalFieldMock, phTwoNumberTwoAdditionalFieldMock, mobileNumberTwoAdditionalFieldMock,
			emailTwoAdditionalFieldMock, addressLine1TwoAdditionalFieldMock, addressLine2TwoAdditionalFieldMock,
			cityTwoAdditionalFieldMock, stateTwoAdditionalFieldMock, postCodeTwoAdditionalFieldMock,
			countryTwoAdditionalFieldMock, governmentIdTypeTwoAdditionalFieldMock,
			governmentIdNumTwoAdditionalFieldMock, governmentIdCountTwoAdditionalFieldMock,
			driversLicenseTwoNumAdditionalFieldMock, driversLicenseCntTwoAdditionalFieldMock;

	@Mock
	private BusinessStakeHolderModel businessStakeHolderModelOneMock, businessStakeHolderModelTwoMock;

	@BeforeEach
	void setUp() {
		final List<String> countriesNotLocalTax = List.of("KOR", "USA");
		ReflectionTestUtils.setField(testObj, COUNTRIES_NOT_LOCAL_TAX, countriesNotLocalTax);
	}

	@Test
	void execute_shouldSetProfileTypeToIndividual() {
		when(miraklShopMock.getDefaultBillingInformation()).thenReturn(miraklDefaultBillingInformationMock);
		when(miraklDefaultBillingInformationMock.getCorporateInformation()).thenReturn(miraklCorporateInformationMock);
		when(miraklDefaultBillingInformationMock.getFiscalInformation()).thenReturn(miraklFiscalInformationMock);
		when(miraklCorporateInformationMock.getCompanyRegistrationName()).thenReturn(CORP_NAME);
		when(miraklCorporateInformationMock.getCompanyRegistrationNumber()).thenReturn(IDENTIFICATION_NUMBER);
		when(miraklFiscalInformationMock.getLocalTaxNumber()).thenReturn(VAT_NUMBER);

		final MiraklStringAdditionalFieldValue businessRegistrationStateProvinceMiraklCustomField = new MiraklStringAdditionalFieldValue();
		businessRegistrationStateProvinceMiraklCustomField.setCode("hw-business-reg-state-province");
		businessRegistrationStateProvinceMiraklCustomField.setValue(STATE_PROVINCE_VALUE);

		final MiraklStringAdditionalFieldValue businessRegistrationCountryMiraklCustomField = new MiraklStringAdditionalFieldValue();
		businessRegistrationCountryMiraklCustomField.setCode("hw-business-reg-country");
		businessRegistrationCountryMiraklCustomField.setValue("US");
		when(miraklShopMock.getAdditionalFieldValues()).thenReturn(List
				.of(businessRegistrationStateProvinceMiraklCustomField, businessRegistrationCountryMiraklCustomField));

		final SellerModel.SellerModelBuilder sellerModelBuilderStub = SellerModel.builder();
		doReturn(sellerModelBuilderStub).when(testObj).getCommonFieldsBuilder(miraklShopMock);

		final SellerModel result = testObj.execute(miraklShopMock);

		verify(testObj).getCommonFieldsBuilder(miraklShopMock);
		assertThat(result.getProfileType()).isEqualTo(SellerProfileType.BUSINESS);
		assertThat(result.getCompanyName()).isEqualTo(CORP_NAME);
		assertThat(result.getCompanyRegistrationNumber()).isEqualTo(IDENTIFICATION_NUMBER);
		assertThat(result.getVatNumber()).isEqualTo(VAT_NUMBER);
		assertThat(result.getBusinessRegistrationStateProvince()).isEqualTo(STATE_PROVINCE_VALUE);
		assertThat(result.getCompanyRegistrationCountry()).isEqualTo("US");
	}

	@Test
	void execute_shouldConvertMiraklBusinessStakeHolderAttributesIntoListOfBusinessStakeHolderModel() {
		when(miraklShopMock.getDefaultBillingInformation()).thenReturn(miraklDefaultBillingInformationMock);
		when(miraklDefaultBillingInformationMock.getCorporateInformation()).thenReturn(miraklCorporateInformationMock);
		when(miraklDefaultBillingInformationMock.getFiscalInformation()).thenReturn(miraklFiscalInformationMock);
		when(miraklShopMock.getId()).thenReturn(CLIENT_ID_1);
		when(miraklCorporateInformationMock.getCompanyRegistrationName()).thenReturn(CORP_NAME);
		when(miraklCorporateInformationMock.getCompanyRegistrationNumber()).thenReturn(IDENTIFICATION_NUMBER);
		when(miraklFiscalInformationMock.getLocalTaxNumber()).thenReturn(VAT_NUMBER);

		final List<MiraklAdditionalFieldValue> additionalFieldValues = List.of(tokenOneAdditionalFieldMock,
				businessOneAdditionalFieldMock, directorOneAdditionalFieldMock, uboOneAdditionalFieldMock,
				smoOneAdditionalFieldMock, firstNameOneAdditionalFieldMock, middleNameOneAdditionalFieldMock,
				lastNameOneAdditionalFieldMock, dobOneAdditionalFieldMock, countryOfBirthOneAdditionalFieldMock,
				nationalityOneAdditionalFieldMock, genderOneAdditionalFieldMock, phoneNumberOneAdditionalFieldMock,
				mobileNumberOneAdditionalFieldMock, emailOneAdditionalFieldMock, addressLine1OneAdditionalFieldMock,
				addressLine2OneAdditionalFieldMock, cityOneAdditionalFieldMock, stateOneAdditionalFieldMock,
				postCodeOneAdditionalFieldMock, countryOneAdditionalFieldMock, governmentIdTypeOneAdditionalFieldMock,
				governmentIdNumOneAdditionalFieldMock, governmentIdCountOneAdditionalFieldMock,
				driversLicenseOneNumAdditionalFieldMock, driversLicenseCntOneAdditionalFieldMock,
				tokenTwoAdditionalFieldMock, businessTwoAdditionalFieldMock, directorTwoAdditionalFieldMock,
				uboTwoAdditionalFieldMock, smoTwoAdditionalFieldMock, firstNameTwoAdditionalFieldMock,
				middleNameTwoAdditionalFieldMock, lastNameTwoAdditionalFieldMock, dobTwoAdditionalFieldMock,
				countryOfBirthTwoAdditionalFieldMock, nationalityTwoAdditionalFieldMock, genderTwoAdditionalFieldMock,
				phTwoNumberTwoAdditionalFieldMock, mobileNumberTwoAdditionalFieldMock, emailTwoAdditionalFieldMock,
				addressLine1TwoAdditionalFieldMock, addressLine2TwoAdditionalFieldMock, cityTwoAdditionalFieldMock,
				stateTwoAdditionalFieldMock, postCodeTwoAdditionalFieldMock, countryTwoAdditionalFieldMock,
				governmentIdTypeTwoAdditionalFieldMock, governmentIdNumTwoAdditionalFieldMock,
				governmentIdCountTwoAdditionalFieldMock, driversLicenseTwoNumAdditionalFieldMock,
				driversLicenseCntTwoAdditionalFieldMock);

		when(miraklShopMock.getAdditionalFieldValues()).thenReturn(additionalFieldValues)
				.thenReturn(additionalFieldValues).thenReturn(additionalFieldValues).thenReturn(additionalFieldValues)
				.thenReturn(additionalFieldValues).thenReturn(List.of());
		when(businessStakeHolderModelConverterMock.convert(Triple.of(additionalFieldValues, 1, CLIENT_ID_1)))
				.thenReturn(businessStakeHolderModelOneMock);
		when(businessStakeHolderModelConverterMock.convert(Triple.of(additionalFieldValues, 2, CLIENT_ID_1)))
				.thenReturn(businessStakeHolderModelTwoMock);

		final SellerModel.SellerModelBuilder sellerModelBuilderStub = SellerModel.builder();
		doReturn(sellerModelBuilderStub).when(testObj).getCommonFieldsBuilder(miraklShopMock);

		final BusinessStakeHolderModel.BusinessStakeHolderModelBuilder businessStakeHolderModelBuilder = BusinessStakeHolderModel
				.builder();
		doReturn(businessStakeHolderModelBuilder).when(businessStakeHolderModelOneMock).toBuilder();
		doReturn(businessStakeHolderModelBuilder).when(businessStakeHolderModelTwoMock).toBuilder();

		testObj.execute(miraklShopMock);

		verify(businessStakeHolderModelConverterMock).convert(Triple.of(additionalFieldValues, 1, CLIENT_ID_1));
		verify(businessStakeHolderModelConverterMock).convert(Triple.of(additionalFieldValues, 2, CLIENT_ID_1));
		verify(businessStakeHolderModelConverterMock).convert(Triple.of(additionalFieldValues, 3, CLIENT_ID_1));
		verify(businessStakeHolderModelConverterMock).convert(Triple.of(additionalFieldValues, 4, CLIENT_ID_1));
		verify(businessStakeHolderModelConverterMock).convert(Triple.of(additionalFieldValues, 5, CLIENT_ID_1));
	}

	@Test
	void isApplicable_shouldReturnTrueWhenMiraklShopIsProfessional() {
		when(miraklShopMock.isProfessional()).thenReturn(true);

		final boolean result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenMiraklShopIsNotProfessional() {
		when(miraklShopMock.isProfessional()).thenReturn(false);

		final boolean result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isFalse();
	}

	@ParameterizedTest
	@ValueSource(strings = { "", " ", "ESP", "FRA" })
	void getTaxNumber_shouldReturnLocalTaxNumberWhenTaxIdentificationCountryIsBlankOrNotMatch(final String country) {
		when(miraklFiscalInformationMock.getLocalTaxNumber()).thenReturn(LOCAL_VAT_NUMBER);
		when(miraklFiscalInformationMock.getTaxIdentificationCountry()).thenReturn(country);

		final String result = testObj.getTaxNumber(miraklFiscalInformationMock);
		assertThat(result).isEqualTo(LOCAL_VAT_NUMBER);
	}

	@Test
	void getTaxNumber_shouldReturnLocalTaxNumberWhenTaxIdentificationCountryIsNull() {
		when(miraklFiscalInformationMock.getLocalTaxNumber()).thenReturn(LOCAL_VAT_NUMBER);
		when(miraklFiscalInformationMock.getTaxIdentificationCountry()).thenReturn(null);

		final String result = testObj.getTaxNumber(miraklFiscalInformationMock);
		assertThat(result).isEqualTo(LOCAL_VAT_NUMBER);
	}

	@ParameterizedTest
	@ValueSource(strings = { "USA", "KOR" })
	void getTaxNumber_shouldReturnTaxIdentificationNumberWhenTaxIdentificationCountryMatch(final String country) {
		when(miraklFiscalInformationMock.getTaxIdentificationNumber()).thenReturn(VAT_NUMBER);
		when(miraklFiscalInformationMock.getTaxIdentificationCountry()).thenReturn(country);

		final String result = testObj.getTaxNumber(miraklFiscalInformationMock);
		assertThat(result).isEqualTo(VAT_NUMBER);
	}

	static class MyMiraklShopToProfessionalSellerModelConverter extends MiraklShopToProfessionalSellerModelConverter {

		protected MyMiraklShopToProfessionalSellerModelConverter(
				final StrategyExecutor<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyExecutor,
				final Converter<Triple<List<MiraklAdditionalFieldValue>, Integer, String>, BusinessStakeHolderModel> pairBusinessStakeHolderModelConverter,
				final SellersMiraklApiConfig sellersMiraklApiConfig, final LanguageConverter languageConversion) {
			super(miraklShopBankAccountModelStrategyExecutor, pairBusinessStakeHolderModelConverter,
					sellersMiraklApiConfig, languageConversion);
		}

		@Override
		public SellerModel.SellerModelBuilder getCommonFieldsBuilder(final MiraklShop source) {
			return super.getCommonFieldsBuilder(source);
		}

	}

}
