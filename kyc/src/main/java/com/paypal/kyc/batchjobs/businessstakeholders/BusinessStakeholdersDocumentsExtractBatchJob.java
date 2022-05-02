package com.paypal.kyc.batchjobs.businessstakeholders;

import com.paypal.infrastructure.batchjob.*;
import org.springframework.stereotype.Service;

/**
 * This job extracts the business stakeholders documents for all the modified shops in
 * Mirakl and uploads the documents to Hyperwallet
 */
@Service
public class BusinessStakeholdersDocumentsExtractBatchJob extends
		AbstractBatchJob<BatchJobContext, BusinessStakeholdersDocumentsExtractBatchJobItem> implements BatchJob {

	private final BusinessStakeholdersDocumentsExtractBatchJobItemProcessor businessStakeholdersDocumentsExtractBatchJobItemProcessor;

	private final BusinessStakeholdersDocumentsExtractBatchJobItemExtractor businessStakeholdersDocumentsExtractBatchJobItemExtractor;

	public BusinessStakeholdersDocumentsExtractBatchJob(
			BusinessStakeholdersDocumentsExtractBatchJobItemProcessor businessStakeholdersDocumentsExtractBatchJobItemProcessor,
			BusinessStakeholdersDocumentsExtractBatchJobItemExtractor businessStakeholdersDocumentsExtractBatchJobItemExtractor) {
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
