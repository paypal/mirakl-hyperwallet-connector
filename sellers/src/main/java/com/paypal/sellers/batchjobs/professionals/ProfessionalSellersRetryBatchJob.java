package com.paypal.sellers.batchjobs.professionals;

import com.paypal.infrastructure.batchjob.*;
import org.springframework.stereotype.Service;

/**
 * Retries items that have failed during the professional sellers extract job
 */
@Service
public class ProfessionalSellersRetryBatchJob
		extends AbstractRetryBatchJob<BatchJobContext, ProfessionalSellerExtractJobItem> {

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
