package com.paypal.invoices.extractioncreditnotes.batchjobs;

import com.paypal.invoices.extractioncommons.batchjobs.AbstractAccountingDocumentBatchJob;
import com.paypal.invoices.extractioncommons.batchjobs.AccountingDocumentBatchJobItemEnricher;
import com.paypal.invoices.extractioncommons.batchjobs.AccountingDocumentBatchJobItemValidator;
import com.paypal.invoices.extractioncommons.batchjobs.AccountingDocumentBatchJobPreProcessor;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import org.springframework.stereotype.Component;

/**
 * Retries items that have failed during the credit notes extract job
 */
@Component
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
