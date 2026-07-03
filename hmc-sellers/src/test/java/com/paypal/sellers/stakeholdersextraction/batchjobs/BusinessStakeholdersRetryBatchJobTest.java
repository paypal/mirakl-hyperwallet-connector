package com.paypal.sellers.stakeholdersextraction.batchjobs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholdersRetryBatchJobTest {

	@InjectMocks
	private BusinessStakeholdersRetryBatchJob testObj;

	@Mock
	private BusinessStakeholdersExtractBatchJobItemProcessor businessStakeholdersExtractBatchJobItemProcessorMock;

	@Mock
	private BusinessStakeholdersRetryBatchJobItemsExtractor businessStakeholdersRetryBatchJobItemsExtractorMock;

	@Test
	void getBatchJobItemProcessor_shouldReturnBatchJobItemProcessor() {
		assertThat(testObj.getBatchJobItemProcessor()).isEqualTo(businessStakeholdersExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_shouldReturnRetryBatchJobItemExtractor() {
		assertThat(testObj.getBatchJobItemsExtractor()).isEqualTo(businessStakeholdersRetryBatchJobItemsExtractorMock);
	}

}
