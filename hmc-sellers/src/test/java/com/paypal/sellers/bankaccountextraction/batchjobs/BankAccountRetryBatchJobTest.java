package com.paypal.sellers.bankaccountextraction.batchjobs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BankAccountRetryBatchJobTest {

	@InjectMocks
	private BankAccountRetryBatchJob testObj;

	@Mock
	private BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessorMock;

	@Mock
	private BankAccountRetryBatchJobItemsExtractor bankAccountRetryBatchJobItemsExtractorMock;

	@Test
	void getBatchJobItemProcessor_shouldReturnBatchJobItemProcessor() {
		assertThat(testObj.getBatchJobItemProcessor()).isEqualTo(bankAccountExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_shouldReturnRetryBatchJobItemExtractor() {
		assertThat(testObj.getBatchJobItemsExtractor()).isEqualTo(bankAccountRetryBatchJobItemsExtractorMock);
	}

}
