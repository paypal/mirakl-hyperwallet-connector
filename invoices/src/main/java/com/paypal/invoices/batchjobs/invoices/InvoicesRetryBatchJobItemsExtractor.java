package com.paypal.invoices.batchjobs.invoices;

import com.paypal.infrastructure.batchjob.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.cache.AllRetryPendingFailedItemCacheFailureResolvePolicy;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheFailureResolvePolicy;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extract invoices for retry from the failed items cache.
 */
@Service
public class InvoicesRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, InvoiceExtractJobItem> {

	private final MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService;

	private final AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicy;

	public InvoicesRetryBatchJobItemsExtractor(BatchJobFailedItemService batchJobFailedItemService,
			BatchJobFailedItemCacheService batchJobFailedItemCacheService,
			MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService,
			AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicy) {
		super(InvoiceExtractJobItem.class, InvoiceExtractJobItem.ITEM_TYPE, batchJobFailedItemService,
				batchJobFailedItemCacheService);
		this.miraklAccountingDocumentInvoicesExtractService = miraklAccountingDocumentInvoicesExtractService;
		this.allRetryPendingFailedItemCacheFailureResolvePolicy = allRetryPendingFailedItemCacheFailureResolvePolicy;
	}

	@Override
	protected Collection<InvoiceExtractJobItem> getItems(List<String> ids) {
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
