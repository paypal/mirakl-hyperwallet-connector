package com.paypal.kyc.sellersdocumentextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractExtractBatchJob;
import org.springframework.stereotype.Component;

/**
 * This job extracts the seller documents for all the modified shops in Mirakl and uploads
 * the documents to Hyperwallet
 */
@Component
public class SellersDocumentsExtractBatchJob
		extends AbstractExtractBatchJob<BatchJobContext, SellersDocumentsExtractBatchJobItem> {

	private final SellersDocumentsExtractBatchJobItemProcessor sellersDocumentsExtractBatchJobItemProcessor;

	private final SellersDocumentsExtractBatchJobItemExtractor sellersDocumentsExtractBatchJobItemExtractor;

	public SellersDocumentsExtractBatchJob(
			final SellersDocumentsExtractBatchJobItemProcessor sellersDocumentsExtractBatchJobItemProcessor,
			final SellersDocumentsExtractBatchJobItemExtractor sellersDocumentsExtractBatchJobItemExtractor) {
		this.sellersDocumentsExtractBatchJobItemProcessor = sellersDocumentsExtractBatchJobItemProcessor;
		this.sellersDocumentsExtractBatchJobItemExtractor = sellersDocumentsExtractBatchJobItemExtractor;
	}

	@Override
	protected BatchJobItemProcessor<BatchJobContext, SellersDocumentsExtractBatchJobItem> getBatchJobItemProcessor() {
		return sellersDocumentsExtractBatchJobItemProcessor;
	}

	@Override
	protected BatchJobItemsExtractor<BatchJobContext, SellersDocumentsExtractBatchJobItem> getBatchJobItemsExtractor() {
		return sellersDocumentsExtractBatchJobItemExtractor;
	}

}
