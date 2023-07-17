package com.paypal.sellers.stakeholdersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractRetryBatchJob;
import org.springframework.stereotype.Component;

/**
 * Retries items that have failed during the business stakeholders extract job
 */
@Component
public class BusinessStakeholdersRetryBatchJob
		extends AbstractRetryBatchJob<BatchJobContext, BusinessStakeholderExtractJobItem> {

	private final BusinessStakeholdersExtractBatchJobItemProcessor businessStakeholdersExtractBatchJobItemProcessor;

	private final BusinessStakeholdersRetryBatchJobItemsExtractor businessStakeholdersRetryBatchJobItemsExtractor;

	public BusinessStakeholdersRetryBatchJob(
			final BusinessStakeholdersExtractBatchJobItemProcessor businessStakeholdersExtractBatchJobItemProcessor,
			final BusinessStakeholdersRetryBatchJobItemsExtractor businessStakeholdersRetryBatchJobItemsExtractor) {
		this.businessStakeholdersExtractBatchJobItemProcessor = businessStakeholdersExtractBatchJobItemProcessor;
		this.businessStakeholdersRetryBatchJobItemsExtractor = businessStakeholdersRetryBatchJobItemsExtractor;
	}

	@Override
	protected BatchJobItemProcessor<BatchJobContext, BusinessStakeholderExtractJobItem> getBatchJobItemProcessor() {
		return businessStakeholdersExtractBatchJobItemProcessor;
	}

	@Override
	protected BatchJobItemsExtractor<BatchJobContext, BusinessStakeholderExtractJobItem> getBatchJobItemsExtractor() {
		return businessStakeholdersRetryBatchJobItemsExtractor;
	}

}
