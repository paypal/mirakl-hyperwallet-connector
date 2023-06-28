package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractRetryBatchJob;
import org.springframework.stereotype.Component;

/**
 * Retries items that have failed during the professional sellers extract job
 */
@Component
public class ProfessionalSellersRetryBatchJob
		extends AbstractRetryBatchJob<BatchJobContext, ProfessionalSellerExtractJobItem> {

	private final ProfessionalSellersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessor;

	private final ProfessionalSellersRetryBatchJobItemsExtractor professionalSellersRetryBatchJobItemsExtractor;

	public ProfessionalSellersRetryBatchJob(
			final ProfessionalSellersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessor,
			final ProfessionalSellersRetryBatchJobItemsExtractor professionalSellersRetryBatchJobItemsExtractor) {
		this.professionalSellersExtractBatchJobItemProcessor = professionalSellersExtractBatchJobItemProcessor;
		this.professionalSellersRetryBatchJobItemsExtractor = professionalSellersRetryBatchJobItemsExtractor;
	}

	@Override
	protected BatchJobItemProcessor<BatchJobContext, ProfessionalSellerExtractJobItem> getBatchJobItemProcessor() {
		return professionalSellersExtractBatchJobItemProcessor;
	}

	@Override
	protected BatchJobItemsExtractor<BatchJobContext, ProfessionalSellerExtractJobItem> getBatchJobItemsExtractor() {
		return professionalSellersRetryBatchJobItemsExtractor;
	}

}
