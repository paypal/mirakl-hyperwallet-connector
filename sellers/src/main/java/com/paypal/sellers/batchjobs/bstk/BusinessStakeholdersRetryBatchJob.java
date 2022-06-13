package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.*;
import org.springframework.stereotype.Service;

/**
 * Retries items that have failed during the business stakeholders extract job
 */
@Service
public class BusinessStakeholdersRetryBatchJob
		extends AbstractRetryBatchJob<BatchJobContext, BusinessStakeholderExtractJobItem> {

	private final BusinessStakeholdersExtractBatchJobItemProcessor businessStakeholdersExtractBatchJobItemProcessor;

	private final BusinessStakeholdersRetryBatchJobItemsExtractor businessStakeholdersRetryBatchJobItemsExtractor;

	public BusinessStakeholdersRetryBatchJob(
			BusinessStakeholdersExtractBatchJobItemProcessor businessStakeholdersExtractBatchJobItemProcessor,
			BusinessStakeholdersRetryBatchJobItemsExtractor businessStakeholdersRetryBatchJobItemsExtractor) {
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
