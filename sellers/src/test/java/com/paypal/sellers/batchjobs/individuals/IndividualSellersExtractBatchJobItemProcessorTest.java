package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.service.TokenSynchronizationService;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletUserServiceStrategyExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualSellersExtractBatchJobItemProcessorTest {

	@InjectMocks
	private IndividualSellersExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutorMock;

	@Mock
	private TokenSynchronizationService<SellerModel> tokenSynchronizationServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	private SellerModel sellerModel;

	private IndividualSellerExtractJobItem individualSellerExtractJobItem;

	@BeforeEach
	void setUp() {
		sellerModel = SellerModel.builder().build();
		individualSellerExtractJobItem = new IndividualSellerExtractJobItem(sellerModel);
		when(tokenSynchronizationServiceMock.synchronizeToken(sellerModel)).thenReturn(sellerModel);
	}

	@Test
	void processItem_ShouldAlwaysExecuteHyperWalletUserServiceStrategyExecutor() {
		when(hyperWalletUserServiceStrategyExecutorMock.execute(sellerModel)).thenReturn(sellerModel);

		testObj.processItem(batchJobContextMock, individualSellerExtractJobItem);

		verify(hyperWalletUserServiceStrategyExecutorMock).execute(sellerModel);
	}

	@Test
	void processItem_shouldSynchronizeSellerBetweenHyperwalletAndMirakl() {
		when(hyperWalletUserServiceStrategyExecutorMock.execute(sellerModel)).thenReturn(sellerModel);

		testObj.processItem(batchJobContextMock, individualSellerExtractJobItem);

		verify(tokenSynchronizationServiceMock).synchronizeToken(sellerModel);
	}

}
