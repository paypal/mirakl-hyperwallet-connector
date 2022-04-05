package com.paypal.sellers.batchjobs.bstk;

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
class BusinessStakeholdersExtractBatchJobTest {

	@InjectMocks
	private BusinessStakeholdersExtractBatchJob testObj;

	@Mock
	private BusinessStakeholdersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessorMock;

	@Mock
	private BusinessStakeholdersExtractBatchJobItemsExtractor professionalSellersExtractBatchJobItemsExtractorMock;

	@Test
	void getBatchJobItemProcessor_ShouldReturnBusinessStakeholdersExtractBatchJobItemProcessor() {

		final BatchJobItemProcessor<BatchJobContext, BusinessStakeholderExtractJobItem> result = testObj
				.getBatchJobItemProcessor();

		assertThat(result).isEqualTo(professionalSellersExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_ShouldReturnBusinessStakeholdersExtractBatchJobItemsExtractor() {

		final BatchJobItemsExtractor<BatchJobContext, BusinessStakeholderExtractJobItem> result = testObj
				.getBatchJobItemsExtractor();

		assertThat(result).isEqualTo(professionalSellersExtractBatchJobItemsExtractorMock);
	}

}
