package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
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
