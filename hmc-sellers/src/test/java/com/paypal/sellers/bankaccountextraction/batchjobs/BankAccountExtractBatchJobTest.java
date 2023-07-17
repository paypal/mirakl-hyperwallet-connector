package com.paypal.sellers.bankaccountextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.sellers.bankaccountextraction.batchjobs.BankAccountExtractBatchJob;
import com.paypal.sellers.bankaccountextraction.batchjobs.BankAccountExtractBatchJobItemProcessor;
import com.paypal.sellers.bankaccountextraction.batchjobs.BankAccountExtractBatchJobItemsExtractor;
import com.paypal.sellers.bankaccountextraction.batchjobs.BankAccountExtractJobItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BankAccountExtractBatchJobTest {

	@InjectMocks
	private BankAccountExtractBatchJob testObj;

	@Mock
	private BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessorMock;

	@Mock
	private BankAccountExtractBatchJobItemsExtractor bankAccountExtractBatchJobItemsExtractorMock;

	@Test
	void getBatchJobItemProcessor_ShouldReturnTheBankAccountExtractBatchJobItemProcessor() {

		final BatchJobItemProcessor<BatchJobContext, BankAccountExtractJobItem> result = testObj
				.getBatchJobItemProcessor();

		assertThat(result).isEqualTo(bankAccountExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_ShouldReturnTheBankAccountExtractBatchJobItemsExtractor() {

		final BatchJobItemsExtractor<BatchJobContext, BankAccountExtractJobItem> result = testObj
				.getBatchJobItemsExtractor();

		assertThat(result).isEqualTo(bankAccountExtractBatchJobItemsExtractorMock);
	}

}
