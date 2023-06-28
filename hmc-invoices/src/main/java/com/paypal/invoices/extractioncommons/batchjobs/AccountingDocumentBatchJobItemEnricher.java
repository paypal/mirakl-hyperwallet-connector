package com.paypal.invoices.extractioncommons.batchjobs;

import com.paypal.infrastructure.itemlinks.model.HyperwalletItemLinkLocator;
import com.paypal.infrastructure.itemlinks.model.HyperwalletItemTypes;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.invoices.extractioncommons.services.AccountingDocumentsLinksService;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemEnricher;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Batch job item enricher for invoice items that enrichs their information adding the
 * bank account and the program token managed by the item links service.
 */
@Component
public class AccountingDocumentBatchJobItemEnricher implements
		BatchJobItemEnricher<BatchJobContext, AbstractAccountingDocumentBatchJobItem<? super AccountingDocumentModel>> {

	private final AccountingDocumentsLinksService accountingDocumentsLinksService;

	public AccountingDocumentBatchJobItemEnricher(
			final AccountingDocumentsLinksService accountingDocumentsLinksService) {
		this.accountingDocumentsLinksService = accountingDocumentsLinksService;
	}

	@Override
	public AbstractAccountingDocumentBatchJobItem enrichItem(final BatchJobContext ctx,
			final AbstractAccountingDocumentBatchJobItem jobItem) {
		return jobItem.from(enrichedItem((AccountingDocumentModel) jobItem.getItem()));
	}

	private AccountingDocumentModel enrichedItem(final AccountingDocumentModel item) {
		final Collection<HyperwalletItemLinkLocator> accountingDocumentLinks = accountingDocumentsLinksService
				.findRequiredLinks(item);

		final String destinationToken = getDestinationToken(accountingDocumentLinks);
		final String hyperwalletProgram = getHyperwalletProgram(accountingDocumentLinks);

		return buildEnriched(item, destinationToken, hyperwalletProgram);
	}

	protected AccountingDocumentModel buildEnriched(final AccountingDocumentModel item, final String destinationToken,
			final String hyperwalletProgram) {
		return item.toBuilder().destinationToken(destinationToken).hyperwalletProgram(hyperwalletProgram).build();
	}

	private String getHyperwalletProgram(final Collection<HyperwalletItemLinkLocator> accountingDocumentLinks) {
		return getLink(accountingDocumentLinks, HyperwalletItemTypes.PROGRAM);
	}

	private String getDestinationToken(final Collection<HyperwalletItemLinkLocator> accountingDocumentLinks) {
		return getLink(accountingDocumentLinks, HyperwalletItemTypes.BANK_ACCOUNT);
	}

	private String getLink(final Collection<HyperwalletItemLinkLocator> accountingDocumentLinks,
			final HyperwalletItemTypes type) {
		//@formatter:off
		return accountingDocumentLinks.stream()
				.filter(locator -> locator.getType().equals(type))
				.map(HyperwalletItemLinkLocator::getId)
				.findAny()
				.orElse(null);
		//@formatter:on
	}

}
