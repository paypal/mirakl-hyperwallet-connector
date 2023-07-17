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
 * Extract invoices job for extracting Mirakl invoices data and populate it on
 * HyperWallet.
 */
@Component
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
