package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.service.MiraklBusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletBusinessStakeHolderStrategyExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholdersExtractBatchJobItemProcessorTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	@InjectMocks
	private BusinessStakeholdersExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletBusinessStakeHolderStrategyExecutor hyperWalletBusinessStakeHolderStrategyExecutorMock;

	@Mock
	private MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void processItem_ShouldProcessBusinessStakeHolderAndUpdateIt_WhenBusinessStakeHolderIsNotNull() {

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder()
				.clientUserId(CLIENT_USER_ID).build();
		final BusinessStakeholderExtractJobItem businessStakeholderExtractJobItem = new BusinessStakeholderExtractJobItem(
				businessStakeHolderModel);

		when(hyperWalletBusinessStakeHolderStrategyExecutorMock.execute(businessStakeHolderModel))
				.thenReturn(businessStakeHolderModel);

		testObj.processItem(batchJobContextMock, businessStakeholderExtractJobItem);

		verify(hyperWalletBusinessStakeHolderStrategyExecutorMock).execute(businessStakeHolderModel);
		verify(miraklBusinessStakeholderExtractServiceMock).updateBusinessStakeholderToken(CLIENT_USER_ID,
				List.of(businessStakeHolderModel));
	}

	@Test
	void processItem_ShouldProcessBusinessStakeHolderAndNotUpdateIt_WhenBusinessStakeHolderIsNull() {

		final BusinessStakeHolderModel businessStakeHolderModel = BusinessStakeHolderModel.builder()
				.clientUserId(CLIENT_USER_ID).build();
		final BusinessStakeholderExtractJobItem businessStakeholderExtractJobItem = new BusinessStakeholderExtractJobItem(
				businessStakeHolderModel);

		when(hyperWalletBusinessStakeHolderStrategyExecutorMock.execute(businessStakeHolderModel)).thenReturn(null);

		testObj.processItem(batchJobContextMock, businessStakeholderExtractJobItem);

		verify(hyperWalletBusinessStakeHolderStrategyExecutorMock).execute(businessStakeHolderModel);
		verify(miraklBusinessStakeholderExtractServiceMock, never()).updateBusinessStakeholderToken(CLIENT_USER_ID,
				List.of(businessStakeHolderModel));
	}

}
