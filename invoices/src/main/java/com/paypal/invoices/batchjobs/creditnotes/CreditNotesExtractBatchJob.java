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
 * Extract invoices job for extracting Mirakl invoices data and populate it on
 * HyperWallet.
 */
@Service
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
