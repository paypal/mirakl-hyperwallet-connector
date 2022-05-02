package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.*;
import org.springframework.stereotype.Service;

/**
 * Extract business stakeholders job for extracting Mirakl professional sellers data and
 * populate it on HyperWallet as users.
 */
@Service
public class BusinessStakeholdersExtractBatchJob
		extends AbstractBatchJob<BatchJobContext, BusinessStakeholderExtractJobItem> implements BatchJob {

	private final BusinessStakeholdersExtractBatchJobItemsExtractor professionalSellersExtractBatchJobItemsExtractor;

	private final BusinessStakeholdersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessor;

	public BusinessStakeholdersExtractBatchJob(
			final BusinessStakeholdersExtractBatchJobItemProcessor professionalSellersExtractBatchJobItemProcessor,
			final BusinessStakeholdersExtractBatchJobItemsExtractor professionalSellersExtractBatchJobItemsExtractor) {
		this.professionalSellersExtractBatchJobItemProcessor = professionalSellersExtractBatchJobItemProcessor;
		this.professionalSellersExtractBatchJobItemsExtractor = professionalSellersExtractBatchJobItemsExtractor;
	}

	/**
	 * Retrieves the business stakeholders batch job item processor
	 * @return the {@link BatchJobItemProcessor} for
	 * {@link BusinessStakeholderExtractJobItem}
	 */
	@Override
	protected BatchJobItemProcessor<BatchJobContext, BusinessStakeholderExtractJobItem> getBatchJobItemProcessor() {
		return this.professionalSellersExtractBatchJobItemProcessor;
	}

	/**
	 * Retrieves the business stakeholders batch job items extractor
	 * @return the {@link BatchJobItemsExtractor} for
	 * {@link BusinessStakeholderExtractJobItem}
	 */
	@Override
	protected BatchJobItemsExtractor<BatchJobContext, BusinessStakeholderExtractJobItem> getBatchJobItemsExtractor() {
		return this.professionalSellersExtractBatchJobItemsExtractor;
	}

}
