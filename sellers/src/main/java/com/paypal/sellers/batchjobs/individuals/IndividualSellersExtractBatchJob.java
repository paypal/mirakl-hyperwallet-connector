package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.AbstractBatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Extract individual sellers job for extracting Mirakl sellers data and populate it on
 * HyperWallet as users
 */
@Service
public class IndividualSellersExtractBatchJob
		extends AbstractBatchJob<BatchJobContext, IndividualSellerExtractJobItem> {

	@Resource
	private IndividualSellersExtractBatchJobItemProcessor individualSellersExtractBatchJobItemProcessor;

	@Resource
	private IndividualSellersExtractBatchJobItemsExtractor individualSellersExtractBatchJobItemsExtractor;

	/**
	 * Retrieves the individual seller batch job item processor
	 * @return the {@link BatchJobItemProcessor} for
	 * {@link IndividualSellerExtractJobItem}
	 */
	@Override
	protected BatchJobItemProcessor<BatchJobContext, IndividualSellerExtractJobItem> getBatchJobItemProcessor() {
		return this.individualSellersExtractBatchJobItemProcessor;
	}

	/**
	 * Retrieves the individual seller batch job items extractor
	 * @return the {@link BatchJobItemsExtractor} for
	 * {@link IndividualSellerExtractJobItem}
	 */
	@Override
	protected BatchJobItemsExtractor<BatchJobContext, IndividualSellerExtractJobItem> getBatchJobItemsExtractor() {
		return this.individualSellersExtractBatchJobItemsExtractor;
	}

}
