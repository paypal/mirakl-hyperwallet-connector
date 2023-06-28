package com.paypal.sellers.bankaccountextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.sellers.bankaccountextraction.services.BankAccountTokenSynchronizationServiceImpl;
import com.paypal.sellers.bankaccountextraction.services.strategies.HyperWalletBankAccountStrategyExecutor;
import com.paypal.sellers.bankaccountextraction.batchjobs.BankAccountExtractBatchJobItemProcessor;
import com.paypal.sellers.bankaccountextraction.batchjobs.BankAccountExtractJobItem;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountExtractBatchJobItemProcessorTest {

	@InjectMocks
	private BankAccountExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletBankAccountStrategyExecutor hyperWalletBankAccountStrategyExecutorMock;

	@Mock
	private BankAccountTokenSynchronizationServiceImpl bankAccountTokenSynchronizationServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void processItem_ShouldExecuteHyperWalletBankAccountServiceExecutor() {

		final SellerModel sellerModel = SellerModel.builder().build();
		final BankAccountExtractJobItem bankAccountExtractJobItem = new BankAccountExtractJobItem(sellerModel);

		final SellerModel synchronizedSellerModel = SellerModel.builder().build();

		when(bankAccountTokenSynchronizationServiceMock.synchronizeToken(sellerModel))
				.thenReturn(synchronizedSellerModel);

		testObj.processItem(batchJobContextMock, bankAccountExtractJobItem);

		verify(hyperWalletBankAccountStrategyExecutorMock).execute(synchronizedSellerModel);
	}

}
