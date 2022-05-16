package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.AbstractBatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
import org.springframework.stereotype.Service;

@Service
public class IndividualSellersRetryBatchJob extends AbstractBatchJob<BatchJobContext, IndividualSellersExtractJobItem> {

	private final IndividualSellersRetryBatchJobItemExtractor individualSellersRetryBatchJobItemExtractor;

	private final IndividualSellersExtractBatchJobItemProcessor individualSellersExtractBatchJobItemProcessor;

	public IndividualSellersRetryBatchJob(
			IndividualSellersRetryBatchJobItemExtractor individualSellersRetryBatchJobItemExtractor,
			IndividualSellersExtractBatchJobItemProcessor individualSellersExtractBatchJobItemProcessor) {
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
