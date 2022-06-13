package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobType;
import com.paypal.invoices.batchjobs.common.AbstractAccountingDocumentBatchJob;
import com.paypal.invoices.batchjobs.common.AccountingDocumentBatchJobItemEnricher;
import com.paypal.invoices.batchjobs.common.AccountingDocumentBatchJobItemValidator;
import com.paypal.invoices.batchjobs.common.AccountingDocumentBatchJobPreProcessor;
import org.springframework.stereotype.Service;

/**
 * Retries items that have failed during the credit notes extract job
 */
@Service
public class CreditNotesRetryBatchJob extends AbstractAccountingDocumentBatchJob<CreditNoteExtractJobItem> {

	private final CreditNotesExtractBatchJobItemProcessor creditNotesExtractBatchJobItemProcessor;

	private final CreditNotesRetryBatchJobItemsExtractor creditNotesRetryBatchJobItemsExtractor;

	public CreditNotesRetryBatchJob(
			final CreditNotesExtractBatchJobItemProcessor creditNotesExtractBatchJobItemProcessor,
			final CreditNotesRetryBatchJobItemsExtractor creditNotesRetryBatchJobItemsExtractor,
			final AccountingDocumentBatchJobItemEnricher accountingDocumentBatchJobItemEnricher,
			final AccountingDocumentBatchJobPreProcessor accountingDocumentBatchJobPreProcessor,
			final AccountingDocumentBatchJobItemValidator accountingDocumentBatchJobItemValidator) {
		super(accountingDocumentBatchJobItemEnricher, accountingDocumentBatchJobPreProcessor,
				accountingDocumentBatchJobItemValidator);
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

	@Override
	public BatchJobType getType() {
		return BatchJobType.RETRY;
	}

}
