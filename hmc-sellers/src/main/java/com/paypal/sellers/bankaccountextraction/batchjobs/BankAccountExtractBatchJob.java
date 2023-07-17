package com.paypal.sellers.bankaccountextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractExtractBatchJob;
import org.springframework.stereotype.Component;

/**
 * Extract bank account job for extracting Mirakl sellers data and populate it on
 * HyperWallet as users
 */
@Component
public class BankAccountExtractBatchJob extends AbstractExtractBatchJob<BatchJobContext, BankAccountExtractJobItem> {

	private final BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessor;

	private final BankAccountExtractBatchJobItemsExtractor bankAccountExtractBatchJobItemsExtractor;

	public BankAccountExtractBatchJob(
			final BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessor,
			final BankAccountExtractBatchJobItemsExtractor bankAccountExtractBatchJobItemsExtractor) {
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
