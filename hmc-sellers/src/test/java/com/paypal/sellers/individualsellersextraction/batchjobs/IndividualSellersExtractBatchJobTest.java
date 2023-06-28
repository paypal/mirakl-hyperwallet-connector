package com.paypal.sellers.individualsellersextraction.batchjobs;

import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersExtractBatchJob;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersExtractBatchJobItemProcessor;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersExtractBatchJobItemsExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class IndividualSellersExtractBatchJobTest {

	@InjectMocks
	private IndividualSellersExtractBatchJob testObj;

	@Mock
	private IndividualSellersExtractBatchJobItemProcessor individualSellersExtractBatchJobItemProcessorMock;

	@Mock
	private IndividualSellersExtractBatchJobItemsExtractor individualSellersExtractBatchJobItemsExtractorMock;

	@Test
	void getBatchJobItemProcessor_ShouldReturnIndividualSellersExtractBatchJobItemProcessor() {

		assertThat(testObj.getBatchJobItemProcessor()).isEqualTo(individualSellersExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_ShouldReturnIndividualSellersExtractBatchJobItemsExtractor() {

		assertThat(testObj.getBatchJobItemsExtractor()).isEqualTo(individualSellersExtractBatchJobItemsExtractorMock);
	}

}
