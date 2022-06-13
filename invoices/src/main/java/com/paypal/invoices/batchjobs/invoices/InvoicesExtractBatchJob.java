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
 * Extract invoices job for extracting Mirakl invoices data and populate it on
 * HyperWallet.
 */
@Service
public class InvoicesExtractBatchJob extends AbstractAccountingDocumentBatchJob<InvoiceExtractJobItem> {

	private final InvoicesExtractBatchJobItemsExtractor invoicesExtractBatchJobItemsExtractor;

	private final InvoicesExtractBatchJobItemProcessor invoicesExtractBatchJobItemProcessor;

	public InvoicesExtractBatchJob(final InvoicesExtractBatchJobItemsExtractor invoicesExtractBatchJobItemsExtractor,
			final InvoicesExtractBatchJobItemProcessor invoicesExtractBatchJobItemProcessor,
			final AccountingDocumentBatchJobItemEnricher accountingDocumentBatchJobItemEnricher,
			final AccountingDocumentBatchJobPreProcessor accountingDocumentBatchJobPreProcessor,
			final AccountingDocumentBatchJobItemValidator accountingDocumentBatchJobItemValidator) {
		super(accountingDocumentBatchJobItemEnricher, accountingDocumentBatchJobPreProcessor,
				accountingDocumentBatchJobItemValidator);
		this.invoicesExtractBatchJobItemsExtractor = invoicesExtractBatchJobItemsExtractor;
		this.invoicesExtractBatchJobItemProcessor = invoicesExtractBatchJobItemProcessor;
	}

	/**
	 * Retrieves the individual seller batch job items extractor
	 * @return the {@link BatchJobItemsExtractor} for {@link InvoiceExtractJobItem}
	 */
	@Override
	protected BatchJobItemsExtractor<BatchJobContext, InvoiceExtractJobItem> getBatchJobItemsExtractor() {
		return this.invoicesExtractBatchJobItemsExtractor;
	}

	/**
	 * Retrieves the individual seller batch job item processor
	 * @return the {@link BatchJobItemProcessor} for {@link InvoiceExtractJobItem}
	 */
	@Override
	protected BatchJobItemProcessor<BatchJobContext, InvoiceExtractJobItem> getBatchJobItemProcessor() {
		return this.invoicesExtractBatchJobItemProcessor;
	}

	@Override
	public BatchJobType getType() {
		return BatchJobType.EXTRACT;
	}

}
