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
 * Extract invoices job for extracting Mirakl invoices data and populate it on
 * HyperWallet.
 */
@Component
public class CreditNotesExtractBatchJob extends AbstractAccountingDocumentBatchJob<CreditNoteExtractJobItem> {

	private final CreditNotesExtractBatchJobItemsExtractor creditNotesExtractBatchJobItemsExtractor;

	private final CreditNotesExtractBatchJobItemProcessor creditNotesExtractBatchJobItemProcessor;

	public CreditNotesExtractBatchJob(
			final CreditNotesExtractBatchJobItemsExtractor creditNotesExtractBatchJobItemsExtractor,
			final CreditNotesExtractBatchJobItemProcessor creditNotesExtractBatchJobItemProcessor,
			final AccountingDocumentBatchJobItemEnricher accountingDocumentBatchJobItemEnricher,
			final AccountingDocumentBatchJobPreProcessor accountingDocumentBatchJobPreProcessor,
			final AccountingDocumentBatchJobItemValidator accountingDocumentBatchJobItemValidator) {
		super(accountingDocumentBatchJobItemEnricher, accountingDocumentBatchJobPreProcessor,
				accountingDocumentBatchJobItemValidator);
		this.creditNotesExtractBatchJobItemsExtractor = creditNotesExtractBatchJobItemsExtractor;
		this.creditNotesExtractBatchJobItemProcessor = creditNotesExtractBatchJobItemProcessor;
	}

	/**
	 * Retrieves the individual seller batch job items extractor
	 * @return the {@link BatchJobItemsExtractor} for {@link CreditNoteExtractJobItem}
	 */
	@Override
	protected BatchJobItemsExtractor<BatchJobContext, CreditNoteExtractJobItem> getBatchJobItemsExtractor() {
		return this.creditNotesExtractBatchJobItemsExtractor;
	}

	/**
	 * Retrieves the individual seller batch job item processor
	 * @return the {@link BatchJobItemProcessor} for {@link CreditNoteExtractJobItem}
	 */
	@Override
	protected BatchJobItemProcessor<BatchJobContext, CreditNoteExtractJobItem> getBatchJobItemProcessor() {
		return this.creditNotesExtractBatchJobItemProcessor;
	}

	@Override
	public BatchJobType getType() {
		return BatchJobType.EXTRACT;
	}

}
