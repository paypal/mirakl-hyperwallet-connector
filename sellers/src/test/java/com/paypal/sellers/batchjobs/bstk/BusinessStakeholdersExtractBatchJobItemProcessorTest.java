package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.service.TokenSynchronizationService;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletBusinessStakeHolderStrategyExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholdersExtractBatchJobItemProcessorTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	@InjectMocks
	private BusinessStakeholdersExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletBusinessStakeHolderStrategyExecutor hyperWalletBusinessStakeHolderStrategyExecutorMock;

	@Mock
	private TokenSynchronizationService<BusinessStakeHolderModel> businessStakeholderTokenSynchronizationServiceImplMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void processItem_ShouldSynchronizedBusinessStakeHolderTokenAndExecuteIt() {

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder()
				.clientUserId(CLIENT_USER_ID).build();
		final BusinessStakeholderExtractJobItem businessStakeholderExtractJobItem = new BusinessStakeholderExtractJobItem(
				businessStakeHolderModel);
		final BusinessStakeHolderModel synchronizedBusinessStakeHolderModel = BusinessStakeHolderModel.builder()
				.clientUserId(CLIENT_USER_ID).build();

		when(businessStakeholderTokenSynchronizationServiceImplMock.synchronizeToken(businessStakeHolderModel))
				.thenReturn(synchronizedBusinessStakeHolderModel);

		testObj.processItem(batchJobContextMock, businessStakeholderExtractJobItem);

		verify(hyperWalletBusinessStakeHolderStrategyExecutorMock).execute(synchronizedBusinessStakeHolderModel);
	}

}
