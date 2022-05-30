package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.AbstractBatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
import org.springframework.stereotype.Service;

/**
 * Retries items that have failed during the credit notes extract job
 */
@Service
public class CreditNotesRetryBatchJob extends AbstractBatchJob<BatchJobContext, CreditNoteExtractJobItem> {

	private final CreditNotesExtractBatchJobItemProcessor creditNotesExtractBatchJobItemProcessor;

	private final CreditNotesRetryBatchJobItemsExtractor creditNotesRetryBatchJobItemsExtractor;

	public CreditNotesRetryBatchJob(CreditNotesExtractBatchJobItemProcessor creditNotesExtractBatchJobItemProcessor,
			CreditNotesRetryBatchJobItemsExtractor creditNotesRetryBatchJobItemsExtractor) {
		this.creditNotesExtractBatchJobItemProcessor = creditNotesExtractBatchJobItemProcessor;
		this.creditNotesRetryBatchJobItemsExtractor = creditNotesRetryBatchJobItemsExtractor;
	}

	@Override
	protected BatchJobItemProcessor<BatchJobContext, CreditNoteExtractJobItem> getBatchJobItemProcessor() {
		return creditNotesExtractBatchJobItemProcessor;
	}

	@Override
	protected BatchJobItemsExtractor<BatchJobContext, CreditNoteExtractJobItem> getBatchJobItemsExtractor() {
		return creditNotesRetryBatchJobItemsExtractor;
	}

}
