package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.AbstractBatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
import org.springframework.stereotype.Service;

/**
 * Retries items that have failed during the business stakeholders extract job
 */
@Service
public class BusinessStakeholdersRetryBatchJob
		extends AbstractBatchJob<BatchJobContext, BusinessStakeholderExtractJobItem> {

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
