package com.paypal.sellers.individualsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersExtractBatchJobItemProcessor;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersExtractJobItem;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersRetryBatchJob;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersRetryBatchJobItemExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class IndividualSellersRetryBatchJobTest {

	@InjectMocks
	private IndividualSellersRetryBatchJob testObj;

	@Mock
	private IndividualSellersRetryBatchJobItemExtractor individualSellersRetryBatchJobItemExtractorMock;

	@Mock
	private IndividualSellersExtractBatchJobItemProcessor individualSellersExtractBatchJobItemProcessorMock;

	@Test
	void getBatchJobItemProcessor_ShouldReturnIndividualSellersExtractBatchJobItemProcessor() {

		final BatchJobItemProcessor<BatchJobContext, IndividualSellersExtractJobItem> result = testObj
				.getBatchJobItemProcessor();

		assertThat(result).isEqualTo(individualSellersExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_ShouldReturnIndividualSellersRetryBatchJobItemExtractor() {

		final BatchJobItemsExtractor<BatchJobContext, IndividualSellersExtractJobItem> result = testObj
				.getBatchJobItemsExtractor();

		assertThat(result).isEqualTo(individualSellersRetryBatchJobItemExtractorMock);
	}

}
