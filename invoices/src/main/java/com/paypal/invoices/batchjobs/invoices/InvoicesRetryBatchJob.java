package com.paypal.invoices.batchjobs.invoices;

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
 * Retries items that have failed during the invoices extract job
 */
@Service
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
