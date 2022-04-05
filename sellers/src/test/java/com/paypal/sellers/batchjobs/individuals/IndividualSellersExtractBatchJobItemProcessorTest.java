package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletUserServiceStrategyExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IndividualSellersExtractBatchJobItemProcessorTest {

	@InjectMocks
	private IndividualSellersExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutorMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void processItem_ShouldExecuteHyperWalletUserServiceStrategyExecutor() {

		final SellerModel sellerModel = SellerModel.builder().build();
		final IndividualSellerExtractJobItem individualSellerExtractJobItem = new IndividualSellerExtractJobItem(
				sellerModel);

		testObj.processItem(batchJobContextMock, individualSellerExtractJobItem);

		verify(hyperWalletUserServiceStrategyExecutorMock).execute(sellerModel);
	}

}
