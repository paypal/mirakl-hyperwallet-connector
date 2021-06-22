package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
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
class ListAdditionalFieldValuesToBusinessStakeHolderModelConverterTest {

	private static final Integer BUSINESS_STAKE_HOLDER_NUMBER = 1;

	private static final String CLIENT_ID = "clientId1";

	@Spy
	@InjectMocks
	private ListAdditionalFieldValuesToBusinessStakeHolderModelConverter testObj;

	@Mock
	private BusinessStakeHolderModel businessStakeHolderModelMock;

	@Mock
	private BusinessStakeHolderModel.BusinessStakeHolderModelBuilder businessStakeHolderModelBuilderMock;

	@Mock
	private MiraklAdditionalFieldValue miraklAdditionalFieldValueOneMock;

	@Test
	void convert_shouldReturnBusinessStakeHolderModelBasedOnValuesOfMiraklShop() {
		final List<MiraklAdditionalFieldValue> miraklAdditionalFieldValues = List.of(miraklAdditionalFieldValueOneMock);
		doReturn(businessStakeHolderModelBuilderMock).when(testObj).getBuilder();
		when(businessStakeHolderModelBuilderMock.userToken(miraklAdditionalFieldValues))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.clientUserId(CLIENT_ID))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.token(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.businessContact(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.director(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.ubo(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.smo(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.firstName(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.middleName(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.lastName(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.dateOfBirth(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.countryOfBirth(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.countryOfNationality(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.gender(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.phoneNumber(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.mobileNumber(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.email(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.governmentId(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.governmentIdType(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.driversLicenseId(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.addressLine1(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.addressLine2(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.city(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.stateProvince(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.country(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.postalCode(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER))
				.thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.build()).thenReturn(businessStakeHolderModelMock);
		when(businessStakeHolderModelBuilderMock.stkId(1)).thenReturn(businessStakeHolderModelBuilderMock);
		when(businessStakeHolderModelBuilderMock.hyperwalletProgram(miraklAdditionalFieldValues))
				.thenReturn(businessStakeHolderModelBuilderMock);

		final BusinessStakeHolderModel result = testObj
				.convert(Triple.of(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER, CLIENT_ID));

		verifyAttributes(miraklAdditionalFieldValues);

		assertThat(result).isEqualTo(businessStakeHolderModelMock);
	}

	private void verifyAttributes(final List<MiraklAdditionalFieldValue> miraklAdditionalFieldValues) {
		verify(businessStakeHolderModelBuilderMock).userToken(miraklAdditionalFieldValues);
		verify(businessStakeHolderModelBuilderMock).hyperwalletProgram(miraklAdditionalFieldValues);
		verify(businessStakeHolderModelBuilderMock).clientUserId(CLIENT_ID);
		verify(businessStakeHolderModelBuilderMock).token(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).businessContact(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).director(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).ubo(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).smo(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).firstName(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).middleName(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).lastName(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).dateOfBirth(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).countryOfBirth(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).countryOfNationality(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).gender(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).phoneNumber(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).mobileNumber(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).email(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).governmentId(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).governmentIdType(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).driversLicenseId(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).addressLine1(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).addressLine2(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).city(miraklAdditionalFieldValues, BUSINESS_STAKE_HOLDER_NUMBER);
		verify(businessStakeHolderModelBuilderMock).stateProvince(miraklAdditionalFieldValues,
				BUSINESS_STAKE_HOLDER_NUMBER);
	}

	@Test
	void getBuilder_shouldReturnAnBusinessStakeHolderModelBuilderInstance() {
		final BusinessStakeHolderModel.BusinessStakeHolderModelBuilder builder = testObj.getBuilder();

		assertThat(builder).isInstanceOf(BusinessStakeHolderModel.BusinessStakeHolderModelBuilder.class);
	}

}
