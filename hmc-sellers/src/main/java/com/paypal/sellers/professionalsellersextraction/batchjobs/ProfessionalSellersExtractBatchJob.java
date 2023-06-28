package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractExtractBatchJob;
import org.springframework.stereotype.Component;

/**
 * Extract professional sellers job for extracting Mirakl professional sellers data and
 * populate it on HyperWallet as users.
 */
@Component
public class ProfessionalSellersExtractBatchJob
		extends AbstractExtractBatchJob<BatchJobContext, ProfessionalSellerExtractJobItem> {

	private final ProfessionalSellersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessor;

	private final ProfessionalSellersExtractBatchJobItemsExtractor professionalSellersExtractBatchJobItemsExtractor;

	public ProfessionalSellersExtractBatchJob(
			final ProfessionalSellersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessor,
			final ProfessionalSellersExtractBatchJobItemsExtractor professionalSellersExtractBatchJobItemsExtractor) {
		this.professionalSellersExtractBatchJobItemProcessor = professionalSellersExtractBatchJobItemProcessor;
		this.professionalSellersExtractBatchJobItemsExtractor = professionalSellersExtractBatchJobItemsExtractor;
	}

	/**
	 * Retrieves the seller batch job item processor
	 * @return the {@link BatchJobItemProcessor} for
	 * {@link ProfessionalSellerExtractJobItem}
	 */
	@Override
	protected BatchJobItemProcessor<BatchJobContext, ProfessionalSellerExtractJobItem> getBatchJobItemProcessor() {
		return this.professionalSellersExtractBatchJobItemProcessor;
	}

	/**
	 * Retrieves the seller batch job items extractor
	 * @return the {@link BatchJobItemsExtractor} for
	 * {@link ProfessionalSellerExtractJobItem}
	 */
	@Override
	protected BatchJobItemsExtractor<BatchJobContext, ProfessionalSellerExtractJobItem> getBatchJobItemsExtractor() {
		return this.professionalSellersExtractBatchJobItemsExtractor;
	}

}
