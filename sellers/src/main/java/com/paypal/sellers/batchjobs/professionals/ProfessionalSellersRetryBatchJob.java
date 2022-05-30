package com.paypal.sellers.batchjobs.professionals;

import com.paypal.infrastructure.batchjob.AbstractBatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
import org.springframework.stereotype.Service;

/**
 * Retries items that have failed during the professional sellers extract job
 */
@Service
public class ProfessionalSellersRetryBatchJob
		extends AbstractBatchJob<BatchJobContext, ProfessionalSellerExtractJobItem> {

	private final ProfessionalSellersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessor;

	private final ProfessionalSellersRetryBatchJobItemsExtractor professionalSellersRetryBatchJobItemsExtractor;

	public ProfessionalSellersRetryBatchJob(
			ProfessionalSellersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessor,
			ProfessionalSellersRetryBatchJobItemsExtractor professionalSellersRetryBatchJobItemsExtractor) {
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
