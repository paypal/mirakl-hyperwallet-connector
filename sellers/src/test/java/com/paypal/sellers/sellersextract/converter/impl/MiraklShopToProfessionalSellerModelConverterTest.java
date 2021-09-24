package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklProfessionalInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.model.SellerProfileType;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklShopToProfessionalSellerModelConverterTest {

	private static final String CORP_NAME = "Globex Corporation";

	private static final String IDENTIFICATION_NUMBER = "123478532";

	private static final String VAT_NUMBER = "vatNumber";

	private static final String CLIENT_ID_1 = "clientID1";

	private static final String STATE_PROVINCE_VALUE = "stateProvince";

	@Spy
	@InjectMocks
	private MiraklShopToProfessionalSellerModelConverter testObj;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private Converter<Triple<List<MiraklAdditionalFieldValue>, Integer, String>, BusinessStakeHolderModel> businessStakeHolderModelConverterMock;

	@Mock
	private MiraklProfessionalInformation miraklProfessionalInformationMock;

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

	@Test
	void execute_shouldSetProfileTypeToIndividual() {
		when(miraklShopMock.getProfessionalInformation()).thenReturn(miraklProfessionalInformationMock);
		when(miraklProfessionalInformationMock.getCorporateName()).thenReturn(CORP_NAME);
		when(miraklProfessionalInformationMock.getIdentificationNumber()).thenReturn(IDENTIFICATION_NUMBER);
		when(miraklProfessionalInformationMock.getTaxIdentificationNumber()).thenReturn(VAT_NUMBER);

		final var businessRegistrationStateProvinceMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		businessRegistrationStateProvinceMiraklCustomField.setCode("hw-business-reg-state-province");
		businessRegistrationStateProvinceMiraklCustomField.setValue(STATE_PROVINCE_VALUE);

		final var businessRegistrationCountryMiraklCustomField = new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue();
		businessRegistrationCountryMiraklCustomField.setCode("hw-business-reg-country");
		businessRegistrationCountryMiraklCustomField.setValue("US");
		when(miraklShopMock.getAdditionalFieldValues()).thenReturn(List
				.of(businessRegistrationStateProvinceMiraklCustomField, businessRegistrationCountryMiraklCustomField));

		final SellerModel.SellerModelBuilder sellerModelBuilderStub = SellerModel.builder();
		doReturn(sellerModelBuilderStub).when(testObj).getCommonFieldsBuilder(miraklShopMock);

		final var result = testObj.execute(miraklShopMock);

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
		when(miraklShopMock.getProfessionalInformation()).thenReturn(miraklProfessionalInformationMock);
		when(miraklProfessionalInformationMock.getCorporateName()).thenReturn(CORP_NAME);
		when(miraklShopMock.getId()).thenReturn(CLIENT_ID_1);
		when(miraklProfessionalInformationMock.getIdentificationNumber()).thenReturn(IDENTIFICATION_NUMBER);
		when(miraklProfessionalInformationMock.getTaxIdentificationNumber()).thenReturn(VAT_NUMBER);

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

		final var result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenMiraklShopIsNotProfessional() {
		when(miraklShopMock.isProfessional()).thenReturn(false);

		final var result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isFalse();
	}

}
