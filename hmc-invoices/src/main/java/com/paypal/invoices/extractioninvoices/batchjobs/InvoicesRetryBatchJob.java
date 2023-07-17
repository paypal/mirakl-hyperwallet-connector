package com.paypal.invoices.extractioninvoices.batchjobs;

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
 * Retries items that have failed during the invoices extract job
 */
@Component
public class InvoicesRetryBatchJob extends AbstractAccountingDocumentBatchJob<InvoiceExtractJobItem> {

	private final InvoicesRetryBatchJobItemsExtractor invoicesRetryBatchJobItemsExtractor;

	private final InvoicesExtractBatchJobItemProcessor invoicesExtractBatchJobItemProcessor;

	public InvoicesRetryBatchJob(final InvoicesRetryBatchJobItemsExtractor invoicesRetryBatchJobItemsExtractor,
			final InvoicesExtractBatchJobItemProcessor invoicesExtractBatchJobItemProcessor,
			final AccountingDocumentBatchJobItemEnricher accountingDocumentBatchJobItemEnricher,
			final AccountingDocumentBatchJobPreProcessor accountingDocumentBatchJobPreProcessor,
			final AccountingDocumentBatchJobItemValidator accountingDocumentBatchJobItemValidator) {
		super(accountingDocumentBatchJobItemEnricher, accountingDocumentBatchJobPreProcessor,
				accountingDocumentBatchJobItemValidator);
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

	@Override
	public BatchJobType getType() {
		return BatchJobType.RETRY;
	}

}
