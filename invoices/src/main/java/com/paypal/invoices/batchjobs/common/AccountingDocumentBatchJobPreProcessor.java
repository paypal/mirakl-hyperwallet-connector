package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobPreProcessor;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.service.hmc.AccountingDocumentsLinksService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Batch job preprocessor for invoice job items that ensures that all required
 * relationships between Mirakl and Hyperwallet items are stored in the item links service
 * before starting the invoice items processing.
 */
@Service
public class AccountingDocumentBatchJobPreProcessor
		implements BatchJobPreProcessor<BatchJobContext, BatchJobItem<? extends AccountingDocumentModel>> {

	private final AccountingDocumentsLinksService accountingDocumentsLinksService;

	public AccountingDocumentBatchJobPreProcessor(AccountingDocumentsLinksService accountingDocumentsLinksService) {
		this.accountingDocumentsLinksService = accountingDocumentsLinksService;
	}

	@Override
	public void prepareForProcessing(BatchJobContext ctx,
			Collection<BatchJobItem<? extends AccountingDocumentModel>> itemsToBeProcessed) {
		//@formatter:off
		Set<AccountingDocumentModel> accountingDocumentModels = itemsToBeProcessed.stream()
				.map(BatchJobItem::getItem)
				.collect(Collectors.toSet());
		//@formatter:on

		accountingDocumentsLinksService.storeRequiredLinks(accountingDocumentModels);
	}

}
