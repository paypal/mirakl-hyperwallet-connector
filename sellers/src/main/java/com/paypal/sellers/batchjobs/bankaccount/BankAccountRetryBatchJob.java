package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.AbstractBatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
import org.springframework.stereotype.Service;

/**
 * Retries items that have failed during the bank account extract job
 */
@Service
public class BankAccountRetryBatchJob extends AbstractBatchJob<BatchJobContext, BankAccountExtractJobItem> {

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
