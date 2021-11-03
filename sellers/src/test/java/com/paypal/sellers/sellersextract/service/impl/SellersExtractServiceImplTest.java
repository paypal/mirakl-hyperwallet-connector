package com.paypal.sellers.sellersextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.sellers.entity.FailedSellersInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.BusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletUserServiceStrategyExecutor;
import com.paypal.sellers.service.FailedEntityInformationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellersExtractServiceImplTest {

	@InjectMocks
	private SellersExtractServiceImpl testObj;

	@Mock
	private Date dateMock;

	@Mock
	private SellerModel individualSellerOneMock, individualSellerTwoMock, individualSellerThreeMock,
			individualFailedSellerMock;

	@Mock
	private SellerModel professionalSellerOneMock, professionalSellerTwoMock, professionalSellerThreeMock,
			professionalFailedSellerMock;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private HyperwalletUser hyperwalletUserOneMock, hyperwalletUserTwoMock, hyperwalletUserThreeMock,
			hyperwalletUserRetryMock;

	@Mock
	private FailedEntityInformationService<FailedSellersInformation> failedEntityInformationServiceMock;

	@Mock
	private HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutorMock;

	@Mock
	private FailedSellersInformation failedSellersInformationMock;

	@Mock
	private BusinessStakeholderExtractService businessStakeHolderExtractService;

	@BeforeEach
	void setUp() {
		testObj = new SellersExtractServiceImpl(miraklSellersExtractServiceMock, failedEntityInformationServiceMock,
				hyperWalletUserServiceStrategyExecutorMock, businessStakeHolderExtractService);
	}

	@Test
	void extractSellers_shouldReturnHyperwalletUsersExtractedFromMirakl() {
		final List<SellerModel> failedSellersList = List.of(professionalFailedSellerMock);
		final List<SellerModel> sellersList = List.of(professionalSellerOneMock, professionalSellerTwoMock,
				professionalSellerThreeMock);

		when(miraklSellersExtractServiceMock.extractSellers(dateMock)).thenReturn(sellersList);
		when(failedEntityInformationServiceMock.getAll()).thenReturn(List.of(failedSellersInformationMock));
		when(failedSellersInformationMock.getShopId()).thenReturn("2001");
		when(miraklSellersExtractServiceMock.extractSellers(List.of("2001"))).thenReturn(failedSellersList);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(professionalSellerOneMock))
				.thenReturn(hyperwalletUserOneMock);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(professionalSellerTwoMock))
				.thenReturn(hyperwalletUserTwoMock);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(professionalSellerThreeMock))
				.thenReturn(hyperwalletUserThreeMock);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(professionalFailedSellerMock))
				.thenReturn(hyperwalletUserRetryMock);

		final List<HyperwalletUser> result = testObj.extractSellers(dateMock);

		verify(hyperWalletUserServiceStrategyExecutorMock).execute(professionalSellerOneMock);
		verify(hyperWalletUserServiceStrategyExecutorMock).execute(professionalSellerTwoMock);
		verify(hyperWalletUserServiceStrategyExecutorMock).execute(professionalSellerThreeMock);
		verify(hyperWalletUserServiceStrategyExecutorMock).execute(professionalFailedSellerMock);
		assertThat(result).containsExactlyInAnyOrder(hyperwalletUserOneMock, hyperwalletUserTwoMock,
				hyperwalletUserThreeMock, hyperwalletUserRetryMock);
	}

	@Test
	void extractIndividuals_shouldReturnIndividualHyperwalletUsersExtractedFromMiraklAndPreviouslyFailedOnes() {
		final List<SellerModel> failedSellersList = List.of(individualFailedSellerMock);
		final List<SellerModel> sellersList = List.of(individualSellerOneMock, individualSellerTwoMock,
				individualSellerThreeMock);
		when(miraklSellersExtractServiceMock.extractIndividuals(dateMock)).thenReturn(sellersList);
		when(failedEntityInformationServiceMock.getAll()).thenReturn(List.of(failedSellersInformationMock));
		when(failedSellersInformationMock.getShopId()).thenReturn("2001");
		when(miraklSellersExtractServiceMock.extractIndividuals(List.of("2001"))).thenReturn(failedSellersList);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(individualSellerOneMock))
				.thenReturn(hyperwalletUserOneMock);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(individualSellerTwoMock))
				.thenReturn(hyperwalletUserTwoMock);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(individualSellerThreeMock))
				.thenReturn(hyperwalletUserThreeMock);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(individualFailedSellerMock))
				.thenReturn(hyperwalletUserRetryMock);

		final List<HyperwalletUser> result = testObj.extractIndividuals(dateMock);

		verify(hyperWalletUserServiceStrategyExecutorMock).execute(individualSellerOneMock);
		verify(hyperWalletUserServiceStrategyExecutorMock).execute(individualSellerTwoMock);
		verify(hyperWalletUserServiceStrategyExecutorMock).execute(individualSellerThreeMock);
		verify(hyperWalletUserServiceStrategyExecutorMock).execute(individualFailedSellerMock);
		assertThat(result).containsExactlyInAnyOrder(hyperwalletUserOneMock, hyperwalletUserTwoMock,
				hyperwalletUserThreeMock, hyperwalletUserRetryMock);
	}

	@Test
	void extractProfessionals_shouldReturnProfessionalHyperwalletUsersExtractedFromMirakl() {
		final List<SellerModel> sellersList = List.of(professionalSellerOneMock, professionalSellerTwoMock,
				professionalSellerThreeMock);
		final List<SellerModel> failedSellersList = List.of(professionalFailedSellerMock);
		when(miraklSellersExtractServiceMock.extractProfessionals(dateMock)).thenReturn(sellersList);
		when(failedEntityInformationServiceMock.getAll()).thenReturn(List.of(failedSellersInformationMock));
		when(failedSellersInformationMock.getShopId()).thenReturn("2001");
		when(miraklSellersExtractServiceMock.extractProfessionals(List.of("2001"))).thenReturn(failedSellersList);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(professionalSellerOneMock))
				.thenReturn(hyperwalletUserOneMock);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(professionalSellerTwoMock))
				.thenReturn(hyperwalletUserTwoMock);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(professionalSellerThreeMock))
				.thenReturn(hyperwalletUserThreeMock);
		when(hyperWalletUserServiceStrategyExecutorMock.execute(professionalFailedSellerMock))
				.thenReturn(hyperwalletUserRetryMock);

		final List<HyperwalletUser> result = testObj.extractProfessionals(dateMock);

		verify(hyperWalletUserServiceStrategyExecutorMock).execute(professionalSellerOneMock);
		verify(hyperWalletUserServiceStrategyExecutorMock).execute(professionalSellerTwoMock);
		verify(hyperWalletUserServiceStrategyExecutorMock).execute(professionalSellerThreeMock);
		verify(hyperWalletUserServiceStrategyExecutorMock).execute(professionalFailedSellerMock);
		assertThat(result).containsExactlyInAnyOrder(hyperwalletUserOneMock, hyperwalletUserTwoMock,
				hyperwalletUserThreeMock, hyperwalletUserRetryMock);
	}

}
