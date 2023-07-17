package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.infrastructure.support.services.TokenSynchronizationService;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import com.paypal.sellers.sellerextractioncommons.services.strategies.HyperWalletUserServiceStrategyExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellersExtractBatchJobItemProcessorTest {

	@InjectMocks
	private ProfessionalSellersExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutorMock;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private TokenSynchronizationService<SellerModel> tokenSynchronizationServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	private SellerModel sellerModel;

	private ProfessionalSellerExtractJobItem professionalSellerExtractJobItem;

	@BeforeEach
	void setUp() {
		sellerModel = SellerModel.builder().build();
		professionalSellerExtractJobItem = new ProfessionalSellerExtractJobItem(sellerModel);
		when(tokenSynchronizationServiceMock.synchronizeToken(sellerModel)).thenReturn(sellerModel);
	}

	@Test
	void processItem_ShouldAlwaysExecuteHyperWalletUserServiceStrategyExecutor() {
		when(hyperWalletUserServiceStrategyExecutorMock.execute(sellerModel)).thenReturn(sellerModel);

		testObj.processItem(batchJobContextMock, professionalSellerExtractJobItem);

		verify(hyperWalletUserServiceStrategyExecutorMock).execute(sellerModel);
	}

	@Test
	void processItem_shouldSynchronizeSellerBetweenHyperwalletAndMirakl() {
		when(hyperWalletUserServiceStrategyExecutorMock.execute(sellerModel)).thenReturn(sellerModel);

		testObj.processItem(batchJobContextMock, professionalSellerExtractJobItem);

		verify(tokenSynchronizationServiceMock).synchronizeToken(sellerModel);
	}

}
