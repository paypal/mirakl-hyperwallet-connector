package com.paypal.sellers.stakeholdersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractExtractBatchJob;
import org.springframework.stereotype.Component;

/**
 * Extract business stakeholders job for extracting Mirakl professional sellers data and
 * populate it on HyperWallet as users.
 */
@Component
public class BusinessStakeholdersExtractBatchJob
		extends AbstractExtractBatchJob<BatchJobContext, BusinessStakeholderExtractJobItem> {

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
