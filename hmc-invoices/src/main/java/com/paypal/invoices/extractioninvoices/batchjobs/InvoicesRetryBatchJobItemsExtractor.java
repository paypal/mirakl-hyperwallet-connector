package com.paypal.invoices.extractioninvoices.batchjobs;

import com.paypal.invoices.extractioncommons.services.MiraklAccountingDocumentExtractService;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjobfailures.services.cache.BatchJobFailedItemCacheService;
import com.paypal.jobsystem.batchjobfailures.services.resolvepolicies.AllRetryPendingFailedItemCacheFailureResolvePolicy;
import com.paypal.jobsystem.batchjobfailures.services.resolvepolicies.BatchJobFailedItemCacheFailureResolvePolicy;
import com.paypal.jobsystem.batchjobfailures.support.AbstractCachingFailedItemsBatchJobItemsExtractor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extract invoices for retry from the failed items cache.
 */
@Component
public class InvoicesRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, InvoiceExtractJobItem> {

	private final MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService;

	private final AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicy;

	public InvoicesRetryBatchJobItemsExtractor(final BatchJobFailedItemService batchJobFailedItemService,
			final BatchJobFailedItemCacheService batchJobFailedItemCacheService,
			final MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService,
			final AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicy) {
		super(InvoiceExtractJobItem.class, InvoiceExtractJobItem.ITEM_TYPE, batchJobFailedItemService,
				batchJobFailedItemCacheService);
		this.miraklAccountingDocumentInvoicesExtractService = miraklAccountingDocumentInvoicesExtractService;
		this.allRetryPendingFailedItemCacheFailureResolvePolicy = allRetryPendingFailedItemCacheFailureResolvePolicy;
	}

	@Override
	protected Collection<InvoiceExtractJobItem> getItems(final List<String> ids) {
		//@formatter:off
		return miraklAccountingDocumentInvoicesExtractService.extractAccountingDocuments(ids)
				.stream()
				.map(InvoiceExtractJobItem::new)
				.collect(Collectors.toList());
		//@formatter:on
	}

	@Override
	protected Optional<BatchJobFailedItemCacheFailureResolvePolicy> getBatchJobFailedItemCacheFailureResolvePolicy() {
		return Optional.of(allRetryPendingFailedItemCacheFailureResolvePolicy);
	}

}
