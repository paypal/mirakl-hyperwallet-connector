package com.paypal.sellers.individualsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractExtractBatchJob;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Extract individual sellers job for extracting Mirakl sellers data and populate it on
 * HyperWallet as users
 */
@Component
public class IndividualSellersExtractBatchJob
		extends AbstractExtractBatchJob<BatchJobContext, IndividualSellersExtractJobItem> {

	@Resource
	private IndividualSellersExtractBatchJobItemProcessor individualSellersExtractBatchJobItemProcessor;

	@Resource
	private IndividualSellersExtractBatchJobItemsExtractor individualSellersExtractBatchJobItemsExtractor;

	/**
	 * Retrieves the individual seller batch job item processor
	 * @return the {@link BatchJobItemProcessor} for
	 * {@link IndividualSellersExtractJobItem}
	 */
	@Override
	protected BatchJobItemProcessor<BatchJobContext, IndividualSellersExtractJobItem> getBatchJobItemProcessor() {
		return this.individualSellersExtractBatchJobItemProcessor;
	}

	/**
	 * Retrieves the individual seller batch job items extractor
	 * @return the {@link BatchJobItemsExtractor} for
	 * {@link IndividualSellersExtractJobItem}
	 */
	@Override
	protected BatchJobItemsExtractor<BatchJobContext, IndividualSellersExtractJobItem> getBatchJobItemsExtractor() {
		return this.individualSellersExtractBatchJobItemsExtractor;
	}

}
