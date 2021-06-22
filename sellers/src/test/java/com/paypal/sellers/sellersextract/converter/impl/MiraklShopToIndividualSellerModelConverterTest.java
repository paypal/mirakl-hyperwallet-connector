package com.paypal.sellers.sellersextract.converter.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.model.SellerProfileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklShopToIndividualSellerModelConverterTest {

	@Spy
	@InjectMocks
	private MiraklShopToIndividualSellerModelConverter testObj;

	@Mock
	private MiraklShop miraklShopMock;

	@Test
	void execute_shouldSetProfileTypeToIndividual() {
		final SellerModel.SellerModelBuilder sellerModelBuilderStub = SellerModel.builder();
		doReturn(sellerModelBuilderStub).when(testObj).getCommonFieldsBuilder(miraklShopMock);

		final var result = testObj.execute(miraklShopMock);

		verify(testObj).getCommonFieldsBuilder(miraklShopMock);
		assertThat(result.getProfileType()).isEqualTo(SellerProfileType.INDIVIDUAL);
	}

	@Test
	void isApplicable_shouldReturnTrueWhenMiraklShopIsNotProfessional() {
		when(miraklShopMock.isProfessional()).thenReturn(false);

		final var result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenMiraklShopIsProfessional() {
		when(miraklShopMock.isProfessional()).thenReturn(true);

		final var result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isFalse();
	}

}
