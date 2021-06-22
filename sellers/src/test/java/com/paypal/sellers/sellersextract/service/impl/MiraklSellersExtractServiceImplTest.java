package com.paypal.sellers.sellersextract.service.impl;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.StrategyFactory;
import com.paypal.sellers.infrastructure.utils.MiraklLoggingErrorsUtil;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklSellersExtractServiceImplTest {

	private static final String TOKEN = "TOKEN";

	private static final String INVIDIVUAL_SHOP_ID = "invidivualShop";

	private static final String PROFESSIONAL_SHOP_ID = "professionalShop";

	private MiraklSellersExtractServiceImpl testObj;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private StrategyFactory<MiraklShop, SellerModel> miraklShopSellerModelStrategyFactory;

	@Mock
	private MiraklShops miraklShops;

	@Mock
	private MiraklShop individualShopMock, professionalShopMock;

	@Mock
	private SellerModel individualSellerModelMock, professionalSellerModelMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private Date dateMock;

	@Captor
	private ArgumentCaptor<MiraklGetShopsRequest> miraklGetShopsRequestArgumentCaptor;

	@BeforeEach
	void setUp() {
		testObj = new MiraklSellersExtractServiceImpl(miraklMarketplacePlatformOperatorApiClientMock,
				miraklShopSellerModelStrategyFactory, mailNotificationUtilMock);
	}

	@Test
	void extractSellers_ShouldReturnIndividualAndProfessionalShopsSellerModels() {
		when(miraklShops.getShops()).thenReturn(List.of(individualShopMock, professionalShopMock));
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestArgumentCaptor.capture()))
				.thenReturn(miraklShops);
		when(miraklShopSellerModelStrategyFactory.execute(individualShopMock)).thenReturn(individualSellerModelMock);
		when(miraklShopSellerModelStrategyFactory.execute(professionalShopMock))
				.thenReturn(professionalSellerModelMock);
		when(individualShopMock.getId()).thenReturn(INVIDIVUAL_SHOP_ID);
		when(professionalShopMock.getId()).thenReturn(PROFESSIONAL_SHOP_ID);
		when(individualSellerModelMock.hasAcceptedTermsAndConditions()).thenReturn(Boolean.TRUE);
		when(professionalSellerModelMock.hasAcceptedTermsAndConditions()).thenReturn(Boolean.TRUE);

		final var result = testObj.extractSellers(dateMock);

		assertThat(result).hasSize(2).contains(individualSellerModelMock, professionalSellerModelMock);
	}

	@Test
	void extractSellers_ShouldReturnIndividualAndProfessionalShopsSellerModels_whenShopIdsAreReceived() {
		when(miraklShops.getShops()).thenReturn(List.of(individualShopMock, professionalShopMock));
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestArgumentCaptor.capture()))
				.thenReturn(miraklShops);
		when(miraklShopSellerModelStrategyFactory.execute(individualShopMock)).thenReturn(individualSellerModelMock);
		when(miraklShopSellerModelStrategyFactory.execute(professionalShopMock))
				.thenReturn(professionalSellerModelMock);

		final var result = testObj.extractSellers(List.of(INVIDIVUAL_SHOP_ID, PROFESSIONAL_SHOP_ID));

		assertThat(result).hasSize(2).contains(individualSellerModelMock, professionalSellerModelMock);
	}

	@Test
	void extractSellers_shouldSendEmailNotification_whenMiraklExceptionIsThrown() {
		final var miraklApiException = new MiraklApiException(new MiraklErrorResponseBean(1, "Something went wrong"));
		doThrow(miraklApiException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.getShops(any(MiraklGetShopsRequest.class));

		testObj.extractSellers(dateMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected getting shop information in Mirakl",
				String.format(
						ERROR_MESSAGE_PREFIX + "Something went wrong getting shop information since [dateMock]%n%s",
						MiraklLoggingErrorsUtil.stringify(miraklApiException)));
	}

	@Test
	void extractSellers_shouldSendEmailNotification_whenMiraklExceptionIsThrownAndShopIdsAreReceived() {
		final var miraklApiException = new MiraklApiException(new MiraklErrorResponseBean(1, "Something went wrong"));
		doThrow(miraklApiException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.getShops(any(MiraklGetShopsRequest.class));

		testObj.extractSellers(List.of(INVIDIVUAL_SHOP_ID, PROFESSIONAL_SHOP_ID));

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected getting shop information in Mirakl",
				String.format(ERROR_MESSAGE_PREFIX
						+ "Something went wrong getting shop information with ids [[invidivualShop, professionalShop]]%n%s",
						MiraklLoggingErrorsUtil.stringify(miraklApiException)));
	}

	@Test
	void extractIndividuals_ShouldReturnIndividualShopsSellerModel_whenShopIdsAreReceived() {
		when(miraklShops.getShops()).thenReturn(List.of(individualShopMock));
		when(individualShopMock.isProfessional()).thenReturn(false);
		when(individualSellerModelMock.hasAcceptedTermsAndConditions()).thenReturn(Boolean.TRUE);
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestArgumentCaptor.capture()))
				.thenReturn(miraklShops);
		when(miraklShopSellerModelStrategyFactory.execute(individualShopMock)).thenReturn(individualSellerModelMock);
		when(individualShopMock.getId()).thenReturn(INVIDIVUAL_SHOP_ID);

		final var result = testObj.extractIndividuals(List.of(INVIDIVUAL_SHOP_ID));

		assertThat(result).containsExactly(individualSellerModelMock);
	}

	@Test
	void extractIndividuals_ShouldReturnIndividualShopsSellerModel() {
		when(miraklShops.getShops()).thenReturn(List.of(individualShopMock));
		when(individualShopMock.isProfessional()).thenReturn(false);
		when(individualSellerModelMock.hasAcceptedTermsAndConditions()).thenReturn(Boolean.TRUE);
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestArgumentCaptor.capture()))
				.thenReturn(miraklShops);
		when(miraklShopSellerModelStrategyFactory.execute(individualShopMock)).thenReturn(individualSellerModelMock);
		when(individualShopMock.getId()).thenReturn(INVIDIVUAL_SHOP_ID);

		final var result = testObj.extractIndividuals(dateMock);

		assertThat(result).containsExactly(individualSellerModelMock);
	}

	@Test
	void extractProfessionals_ShouldReturnProfessionalShopsSellerModel() {
		when(miraklShops.getShops()).thenReturn(List.of(professionalShopMock));
		when(professionalShopMock.isProfessional()).thenReturn(true);
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestArgumentCaptor.capture()))
				.thenReturn(miraklShops);
		when(miraklShopSellerModelStrategyFactory.execute(professionalShopMock))
				.thenReturn(professionalSellerModelMock);
		when(professionalSellerModelMock.hasAcceptedTermsAndConditions()).thenReturn(Boolean.TRUE);

		final var result = testObj.extractProfessionals(dateMock);

		assertThat(result).containsExactly(professionalSellerModelMock);
	}

	@Test
	void extractProfessionals_ShouldReturnProfessionalShopsSellerModel_whenShopIdsAreReceived() {
		when(miraklShops.getShops()).thenReturn(List.of(professionalShopMock));
		when(professionalShopMock.isProfessional()).thenReturn(true);
		when(professionalSellerModelMock.hasAcceptedTermsAndConditions()).thenReturn(Boolean.TRUE);
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestArgumentCaptor.capture()))
				.thenReturn(miraklShops);
		when(miraklShopSellerModelStrategyFactory.execute(professionalShopMock))
				.thenReturn(professionalSellerModelMock);

		final var result = testObj.extractProfessionals(List.of(PROFESSIONAL_SHOP_ID));

		assertThat(result).containsExactly(professionalSellerModelMock);
	}

}
