package com.paypal.kyc.batchjobs.sellers;

import com.paypal.infrastructure.batchjob.*;
import org.springframework.stereotype.Service;

/**
 * This job extracts the seller documents for all the modified shops in Mirakl and uploads
 * the documents to Hyperwallet
 */
@Service
public class SellersDocumentsExtractBatchJob
		extends AbstractExtractBatchJob<BatchJobContext, SellersDocumentsExtractBatchJobItem> {

	private final SellersDocumentsExtractBatchJobItemProcessor sellersDocumentsExtractBatchJobItemProcessor;

	private final SellersDocumentsExtractBatchJobItemExtractor sellersDocumentsExtractBatchJobItemExtractor;

	public SellersDocumentsExtractBatchJob(
			SellersDocumentsExtractBatchJobItemProcessor sellersDocumentsExtractBatchJobItemProcessor,
			SellersDocumentsExtractBatchJobItemExtractor sellersDocumentsExtractBatchJobItemExtractor) {
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
