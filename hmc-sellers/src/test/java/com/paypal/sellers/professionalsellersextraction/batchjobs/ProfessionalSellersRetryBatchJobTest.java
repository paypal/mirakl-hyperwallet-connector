package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersExtractBatchJobItemProcessor;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersRetryBatchJob;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersRetryBatchJobItemsExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellersRetryBatchJobTest {

	@InjectMocks
	private ProfessionalSellersRetryBatchJob testObj;

	@Mock
	private ProfessionalSellersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessorMock;

	@Mock
	private ProfessionalSellersRetryBatchJobItemsExtractor professionalSellersRetryBatchJobItemsExtractorMock;

	@Test
	void getBatchJobItemProcessor_shouldReturnBatchJobItemProcessor() {
		assertThat(testObj.getBatchJobItemProcessor()).isEqualTo(professionalSellersExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_shouldReturnRetryBatchJobItemExtractor() {
		assertThat(testObj.getBatchJobItemsExtractor()).isEqualTo(professionalSellersRetryBatchJobItemsExtractorMock);
	}

}
