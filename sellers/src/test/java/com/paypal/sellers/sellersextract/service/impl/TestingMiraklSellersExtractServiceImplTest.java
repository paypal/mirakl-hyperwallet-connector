package com.paypal.sellers.sellersextract.service.impl;

import com.paypal.sellers.infrastructure.configuration.SellersMiraklApiConfig;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestingMiraklSellersExtractServiceImplTest {

	@Spy
	@InjectMocks
	private TestingMiraklSellersExtractServiceImpl testObj;

	@Mock
	private Date dateMock;

	@Mock
	private SellersMiraklApiConfig sellersMiraklApiConfigMock;

	@Mock
	private SellerModel individualSellerModelOneMock, individualSellerModelTwoMock;

	@Mock
	private SellerModel professionalSellerModelOneMock, professionalSellerModelTwoMock;

	@Test
	void extractIndividuals_shouldReturnAnEmptyList() {
		final List<SellerModel> superReturnResult = List.of(this.individualSellerModelOneMock,
				individualSellerModelTwoMock);
		doReturn(superReturnResult).when(testObj).callSuperExtractIndividuals(dateMock);

		when(sellersMiraklApiConfigMock.getTestingDelay()).thenReturn(0L);

		final List<SellerModel> result = testObj.extractIndividuals(dateMock);

		assertThat(result).isSameAs(superReturnResult);
	}

	@Test
	void extractProfessionals_shouldReturnAnEmptyList() {
		final List<SellerModel> superReturnResult = List.of(this.professionalSellerModelOneMock,
				professionalSellerModelTwoMock);
		doReturn(superReturnResult).when(testObj).callSuperExtractProfessionals(dateMock);

		when(sellersMiraklApiConfigMock.getTestingDelay()).thenReturn(0L);

		final List<SellerModel> result = testObj.extractProfessionals(dateMock);

		assertThat(result).isSameAs(superReturnResult);
	}

}
