package com.paypal.sellers.stakeholdersextraction.batchjobs;

import com.paypal.sellers.stakeholdersextraction.batchjobs.BusinessStakeholdersExtractBatchJobItemProcessor;
import com.paypal.sellers.stakeholdersextraction.batchjobs.BusinessStakeholdersRetryBatchJob;
import com.paypal.sellers.stakeholdersextraction.batchjobs.BusinessStakeholdersRetryBatchJobItemsExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

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
