package com.paypal.sellers.batchjobs.professionals;

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
class ProfessionalSellersExtractBatchJobItemProcessorTest {

	@InjectMocks
	private ProfessionalSellersExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutorMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void processItem_ShouldProcessItem() {

		final SellerModel sellerModel = SellerModel.builder().build();
		final ProfessionalSellerExtractJobItem professionalSellerExtractJobItem = new ProfessionalSellerExtractJobItem(
				sellerModel);

		testObj.processItem(batchJobContextMock, professionalSellerExtractJobItem);

		verify(hyperWalletUserServiceStrategyExecutorMock).execute(sellerModel);
	}

}
