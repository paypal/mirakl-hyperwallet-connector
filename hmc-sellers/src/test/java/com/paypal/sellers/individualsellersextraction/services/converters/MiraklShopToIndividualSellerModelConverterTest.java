package com.paypal.sellers.individualsellersextraction.services.converters;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import com.paypal.sellers.sellerextractioncommons.configuration.SellersMiraklApiConfig;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerProfileType;
import com.paypal.sellers.utils.LanguageConverter;
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
	private MyMiraklShopToIndividualSellerModelConverter testObj;

	@Mock
	private MiraklShop miraklShopMock;

	@Test
	void execute_shouldSetProfileTypeToIndividual() {
		final SellerModel.SellerModelBuilder sellerModelBuilderStub = SellerModel.builder();
		doReturn(sellerModelBuilderStub).when(testObj).getCommonFieldsBuilder(miraklShopMock);

		final SellerModel result = testObj.execute(miraklShopMock);

		verify(testObj).getCommonFieldsBuilder(miraklShopMock);
		assertThat(result.getProfileType()).isEqualTo(SellerProfileType.INDIVIDUAL);
	}

	@Test
	void isApplicable_shouldReturnTrueWhenMiraklShopIsNotProfessional() {
		when(miraklShopMock.isProfessional()).thenReturn(false);

		final boolean result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenMiraklShopIsProfessional() {
		when(miraklShopMock.isProfessional()).thenReturn(true);

		final boolean result = testObj.isApplicable(miraklShopMock);

		assertThat(result).isFalse();
	}

	static class MyMiraklShopToIndividualSellerModelConverter extends MiraklShopToIndividualSellerModelConverter {

		protected MyMiraklShopToIndividualSellerModelConverter(
				final StrategyExecutor<MiraklShop, BankAccountModel> miraklShopBankAccountModelStrategyExecutor,
				final SellersMiraklApiConfig sellersMiraklApiConfig, final LanguageConverter languageConversion) {
			super(miraklShopBankAccountModelStrategyExecutor, sellersMiraklApiConfig, languageConversion);
		}

		@Override
		public SellerModel.SellerModelBuilder getCommonFieldsBuilder(final MiraklShop source) {
			return super.getCommonFieldsBuilder(source);
		}

	}

}
