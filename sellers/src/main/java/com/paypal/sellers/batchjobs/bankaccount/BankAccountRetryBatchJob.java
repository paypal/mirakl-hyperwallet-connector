package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.*;
import org.springframework.stereotype.Service;

/**
 * Retries items that have failed during the bank account extract job
 */
@Service
public class BankAccountRetryBatchJob extends AbstractRetryBatchJob<BatchJobContext, BankAccountExtractJobItem> {

	private final BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessor;

	private final BankAccountRetryBatchJobItemsExtractor bankAccountRetryBatchJobItemsExtractor;

	public BankAccountRetryBatchJob(BankAccountExtractBatchJobItemProcessor bankAccountExtractBatchJobItemProcessor,
			BankAccountRetryBatchJobItemsExtractor bankAccountRetryBatchJobItemsExtractor) {
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
