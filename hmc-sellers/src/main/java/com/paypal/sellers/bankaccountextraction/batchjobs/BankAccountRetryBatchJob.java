package com.paypal.sellers.bankaccountextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractRetryBatchJob;
import org.springframework.stereotype.Component;

/**
 * Retries items that have failed during the bank account extract job
 */
@Component
public class BankAccountRetryBatchJob extends AbstractRetryBatchJob<BatchJobContext, BankAccountExtractJobItem> {

	private final BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessor;

	private final BankAccountRetryBatchJobItemsExtractor bankAccountRetryBatchJobItemsExtractor;

	public BankAccountRetryBatchJob(
			final BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessor,
			final BankAccountRetryBatchJobItemsExtractor bankAccountRetryBatchJobItemsExtractor) {
		this.bankAccountExtractBatchJobItemProcessor = bankAccountExtractBatchJobItemProcessor;
		this.bankAccountRetryBatchJobItemsExtractor = bankAccountRetryBatchJobItemsExtractor;
	}

	@Override
	protected BatchJobItemProcessor<BatchJobContext, BankAccountExtractJobItem> getBatchJobItemProcessor() {
		return bankAccountExtractBatchJobItemProcessor;
	}

	@Override
	protected BatchJobItemsExtractor<BatchJobContext, BankAccountExtractJobItem> getBatchJobItemsExtractor() {
		return bankAccountRetryBatchJobItemsExtractor;
	}

}
