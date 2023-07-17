package com.paypal.kyc.stakeholdersdocumentextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractExtractBatchJob;
import org.springframework.stereotype.Component;

/**
 * This job extracts the business stakeholders documents for all the modified shops in
 * Mirakl and uploads the documents to Hyperwallet
 */
@Component
public class BusinessStakeholdersDocumentsExtractBatchJob
		extends AbstractExtractBatchJob<BatchJobContext, BusinessStakeholdersDocumentsExtractBatchJobItem> {

	private final BusinessStakeholdersDocumentsExtractBatchJobItemProcessor businessStakeholdersDocumentsExtractBatchJobItemProcessor;

	private final BusinessStakeholdersDocumentsExtractBatchJobItemExtractor businessStakeholdersDocumentsExtractBatchJobItemExtractor;

	public BusinessStakeholdersDocumentsExtractBatchJob(
			final BusinessStakeholdersDocumentsExtractBatchJobItemProcessor businessStakeholdersDocumentsExtractBatchJobItemProcessor,
			final BusinessStakeholdersDocumentsExtractBatchJobItemExtractor businessStakeholdersDocumentsExtractBatchJobItemExtractor) {
		this.businessStakeholdersDocumentsExtractBatchJobItemProcessor = businessStakeholdersDocumentsExtractBatchJobItemProcessor;
		this.businessStakeholdersDocumentsExtractBatchJobItemExtractor = businessStakeholdersDocumentsExtractBatchJobItemExtractor;
	}

	@Override
	protected BatchJobItemProcessor<BatchJobContext, BusinessStakeholdersDocumentsExtractBatchJobItem> getBatchJobItemProcessor() {
		return businessStakeholdersDocumentsExtractBatchJobItemProcessor;
	}

	@Override
	protected BatchJobItemsExtractor<BatchJobContext, BusinessStakeholdersDocumentsExtractBatchJobItem> getBatchJobItemsExtractor() {
		return businessStakeholdersDocumentsExtractBatchJobItemExtractor;
	}

}
