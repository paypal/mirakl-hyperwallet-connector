package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellerExtractJobItem;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersExtractBatchJob;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersExtractBatchJobItemProcessor;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersExtractBatchJobItemsExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellersExtractBatchJobTest {

	@InjectMocks
	private ProfessionalSellersExtractBatchJob testObj;

	@Mock
	private ProfessionalSellersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessorMock;

	@Mock
	private ProfessionalSellersExtractBatchJobItemsExtractor professionalSellersExtractBatchJobItemsExtractorMock;

	@Test
	void getBatchJobItemProcessor_ShouldReturnProfessionalSellersExtractBatchJobItemProcessor() {

		final BatchJobItemProcessor<BatchJobContext, ProfessionalSellerExtractJobItem> result = testObj
				.getBatchJobItemProcessor();

		assertThat(result).isEqualTo(professionalSellersExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_ShouldReturnBusinessStakeholdersExtractBatchJobItemsExtractor() {

		final BatchJobItemsExtractor<BatchJobContext, ProfessionalSellerExtractJobItem> result = testObj
				.getBatchJobItemsExtractor();

		assertThat(result).isEqualTo(professionalSellersExtractBatchJobItemsExtractorMock);
	}

}
