package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.*;
import org.springframework.stereotype.Service;

/**
 * Extract bank account job for extracting Mirakl sellers data and populate it on
 * HyperWallet as users
 */
@Service
public class BankAccountExtractBatchJob extends AbstractBatchJob<BatchJobContext, BankAccountExtractJobItem> {

	private final BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessor;

	private final BankAccountExtractBatchJobItemsExtractor bankAccountExtractBatchJobItemsExtractor;

	public BankAccountExtractBatchJob(BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessor,
			BankAccountExtractBatchJobItemsExtractor bankAccountExtractBatchJobItemsExtractor) {
		this.bankAccountExtractBatchJobItemProcessor = bankAccountExtractBatchJobItemProcessor;
		this.bankAccountExtractBatchJobItemsExtractor = bankAccountExtractBatchJobItemsExtractor;
	}

	/**
	 * Retrieves the bank account batch job item processor
	 * @return the {@link BatchJobItemProcessor} for {@link BankAccountExtractJobItem}
	 */
	@Override
	protected BatchJobItemProcessor<BatchJobContext, BankAccountExtractJobItem> getBatchJobItemProcessor() {
		return this.bankAccountExtractBatchJobItemProcessor;
	}

	/**
	 * Retrieves the bank account batch job items extractor
	 * @return the {@link BatchJobItemsExtractor} for {@link BankAccountExtractJobItem}
	 */
	@Override
	protected BatchJobItemsExtractor<BatchJobContext, BankAccountExtractJobItem> getBatchJobItemsExtractor() {
		return this.bankAccountExtractBatchJobItemsExtractor;
	}

}
