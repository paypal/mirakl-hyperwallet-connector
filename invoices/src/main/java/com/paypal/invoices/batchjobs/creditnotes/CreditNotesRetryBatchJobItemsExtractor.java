package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.cache.AllRetryPendingFailedItemCacheFailureResolvePolicy;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheFailureResolvePolicy;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extract credit notes for retry from the failed items cache.
 */
@Service
public class CreditNotesRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, CreditNoteExtractJobItem> {

	private final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractService;

	private final AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicy;

	public CreditNotesRetryBatchJobItemsExtractor(BatchJobFailedItemService batchJobFailedItemService,
			BatchJobFailedItemCacheService batchJobFailedItemCacheService,
			MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractService,
			AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicy) {
		super(CreditNoteExtractJobItem.class, CreditNoteExtractJobItem.ITEM_TYPE, batchJobFailedItemService,
				batchJobFailedItemCacheService);
		this.miraklAccountingDocumentCreditNotesExtractService = miraklAccountingDocumentCreditNotesExtractService;
		this.allRetryPendingFailedItemCacheFailureResolvePolicy = allRetryPendingFailedItemCacheFailureResolvePolicy;
	}

	@Override
	protected Collection<CreditNoteExtractJobItem> getItems(List<String> ids) {
		//@formatter:off
		return miraklAccountingDocumentCreditNotesExtractService.extractAccountingDocuments(ids)
				.stream()
				.map(CreditNoteExtractJobItem::new)
				.collect(Collectors.toList());
		//@formatter:on
	}

	@Override
	protected Optional<BatchJobFailedItemCacheFailureResolvePolicy> getBatchJobFailedItemCacheFailureResolvePolicy() {
		return Optional.of(allRetryPendingFailedItemCacheFailureResolvePolicy);
	}

}
