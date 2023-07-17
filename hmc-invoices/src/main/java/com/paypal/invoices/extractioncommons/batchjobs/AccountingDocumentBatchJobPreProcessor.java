package com.paypal.invoices.extractioncommons.batchjobs;

import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.invoices.extractioncommons.services.AccountingDocumentsLinksService;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobPreProcessor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Batch job preprocessor for invoice job items that ensures that all required
 * relationships between Mirakl and Hyperwallet items are stored in the item links service
 * before starting the invoice items processing.
 */
@Component
public class AccountingDocumentBatchJobPreProcessor
		implements BatchJobPreProcessor<BatchJobContext, BatchJobItem<? extends AccountingDocumentModel>> {

	private final AccountingDocumentsLinksService accountingDocumentsLinksService;

	public AccountingDocumentBatchJobPreProcessor(
			final AccountingDocumentsLinksService accountingDocumentsLinksService) {
		this.accountingDocumentsLinksService = accountingDocumentsLinksService;
	}

	@Override
	public void prepareForProcessing(final BatchJobContext ctx,
			final Collection<BatchJobItem<? extends AccountingDocumentModel>> itemsToBeProcessed) {
		//@formatter:off
		final Set<AccountingDocumentModel> accountingDocumentModels = itemsToBeProcessed.stream()
				.map(BatchJobItem::getItem)
				.collect(Collectors.toSet());
		//@formatter:on

		accountingDocumentsLinksService.storeRequiredLinks(accountingDocumentModels);
	}

}
