package com.paypal.invoices.batchjobs.invoices;

import com.paypal.infrastructure.batchjob.AbstractBatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
import org.springframework.stereotype.Service;

/**
 * Retries items that have failed during the invoices extract job
 */
@Service
public class InvoicesRetryBatchJob extends AbstractBatchJob<BatchJobContext, InvoiceExtractJobItem> {

	private final InvoicesRetryBatchJobItemsExtractor invoicesRetryBatchJobItemsExtractor;

	private final InvoicesExtractBatchJobItemProcessor invoicesExtractBatchJobItemProcessor;

	public InvoicesRetryBatchJob(InvoicesRetryBatchJobItemsExtractor invoicesRetryBatchJobItemsExtractor,
			InvoicesExtractBatchJobItemProcessor invoicesExtractBatchJobItemProcessor) {
		this.invoicesRetryBatchJobItemsExtractor = invoicesRetryBatchJobItemsExtractor;
		this.invoicesExtractBatchJobItemProcessor = invoicesExtractBatchJobItemProcessor;
	}

	@Override
	protected BatchJobItemProcessor<BatchJobContext, InvoiceExtractJobItem> getBatchJobItemProcessor() {
		return invoicesExtractBatchJobItemProcessor;
	}

	@Override
	protected BatchJobItemsExtractor<BatchJobContext, InvoiceExtractJobItem> getBatchJobItemsExtractor() {
		return invoicesRetryBatchJobItemsExtractor;
	}

}
