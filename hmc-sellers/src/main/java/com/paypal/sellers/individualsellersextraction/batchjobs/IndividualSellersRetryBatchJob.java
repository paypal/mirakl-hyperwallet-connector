package com.paypal.sellers.individualsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractRetryBatchJob;
import org.springframework.stereotype.Component;

/**
 * Retries items that have failed during the individual sellers extract job
 */
@Component
public class IndividualSellersRetryBatchJob
		extends AbstractRetryBatchJob<BatchJobContext, IndividualSellersExtractJobItem> {

	private final IndividualSellersRetryBatchJobItemExtractor individualSellersRetryBatchJobItemExtractor;

	private final IndividualSellersExtractBatchJobItemProcessor individualSellersExtractBatchJobItemProcessor;

	public IndividualSellersRetryBatchJob(
			final IndividualSellersRetryBatchJobItemExtractor individualSellersRetryBatchJobItemExtractor,
			final IndividualSellersExtractBatchJobItemProcessor individualSellersExtractBatchJobItemProcessor) {
		this.individualSellersRetryBatchJobItemExtractor = individualSellersRetryBatchJobItemExtractor;
		this.individualSellersExtractBatchJobItemProcessor = individualSellersExtractBatchJobItemProcessor;
	}

	/**
	 * Retrieves the individual seller batch job item processor
	 * @return the {@link BatchJobItemProcessor} for
	 * {@link IndividualSellersExtractJobItem}
	 */
	@Override
	protected BatchJobItemProcessor<BatchJobContext, IndividualSellersExtractJobItem> getBatchJobItemProcessor() {
		return individualSellersExtractBatchJobItemProcessor;
	}

	/**
	 * Retrieves the individual seller batch job items extractor
	 * @return the {@link BatchJobItemsExtractor} for
	 * {@link IndividualSellersExtractJobItem}
	 */
	@Override
	protected BatchJobItemsExtractor<BatchJobContext, IndividualSellersExtractJobItem> getBatchJobItemsExtractor() {
		return individualSellersRetryBatchJobItemExtractor;
	}

}
