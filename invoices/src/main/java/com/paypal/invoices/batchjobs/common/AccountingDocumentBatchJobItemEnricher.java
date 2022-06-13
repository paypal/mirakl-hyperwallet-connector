package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemEnricher;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.service.hmc.AccountingDocumentsLinksService;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Batch job item enricher for invoice items that enrichs their information adding the
 * bank account and the program token managed by the item links service.
 */
@Service
public class AccountingDocumentBatchJobItemEnricher implements
		BatchJobItemEnricher<BatchJobContext, AbstractAccountingDocumentBatchJobItem<? super AccountingDocumentModel>> {

	private final AccountingDocumentsLinksService accountingDocumentsLinksService;

	public AccountingDocumentBatchJobItemEnricher(AccountingDocumentsLinksService accountingDocumentsLinksService) {
		this.accountingDocumentsLinksService = accountingDocumentsLinksService;
	}

	@Override
	public AbstractAccountingDocumentBatchJobItem enrichItem(BatchJobContext ctx,
			AbstractAccountingDocumentBatchJobItem jobItem) {
		return jobItem.from(enrichedItem((AccountingDocumentModel) jobItem.getItem()));
	}

	private AccountingDocumentModel enrichedItem(AccountingDocumentModel item) {
		Collection<HyperwalletItemLinkLocator> accountingDocumentLinks = accountingDocumentsLinksService
				.findRequiredLinks(item);

		String destinationToken = getDestinationToken(accountingDocumentLinks);
		String hyperwalletProgram = getHyperwalletProgram(accountingDocumentLinks);

		return buildEnriched(item, destinationToken, hyperwalletProgram);
	}

	protected AccountingDocumentModel buildEnriched(AccountingDocumentModel item, String destinationToken,
			String hyperwalletProgram) {
		return item.toBuilder().destinationToken(destinationToken).hyperwalletProgram(hyperwalletProgram).build();
	}

	private String getHyperwalletProgram(Collection<HyperwalletItemLinkLocator> accountingDocumentLinks) {
		return getLink(accountingDocumentLinks, HyperwalletItemTypes.PROGRAM);
	}

	private String getDestinationToken(Collection<HyperwalletItemLinkLocator> accountingDocumentLinks) {
		return getLink(accountingDocumentLinks, HyperwalletItemTypes.BANK_ACCOUNT);
	}

	private String getLink(Collection<HyperwalletItemLinkLocator> accountingDocumentLinks, HyperwalletItemTypes type) {
		//@formatter:off
		return accountingDocumentLinks.stream()
				.filter(locator -> locator.getType().equals(type))
				.map(HyperwalletItemLinkLocator::getId)
				.findAny()
				.orElse(null);
		//@formatter:on
	}

}
