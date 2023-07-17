package com.paypal.invoices.extractioncreditnotes.batchjobs;

import com.paypal.invoices.extractioncommons.services.MiraklAccountingDocumentExtractService;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
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
 * Extract credit notes for retry from the failed items cache.
 */
@Component
public class CreditNotesRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, CreditNoteExtractJobItem> {

	private final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractService;

	private final AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicy;

	public CreditNotesRetryBatchJobItemsExtractor(final BatchJobFailedItemService batchJobFailedItemService,
			final BatchJobFailedItemCacheService batchJobFailedItemCacheService,
			final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractService,
			final AllRetryPendingFailedItemCacheFailureResolvePolicy allRetryPendingFailedItemCacheFailureResolvePolicy) {
		super(CreditNoteExtractJobItem.class, CreditNoteExtractJobItem.ITEM_TYPE, batchJobFailedItemService,
				batchJobFailedItemCacheService);
		this.miraklAccountingDocumentCreditNotesExtractService = miraklAccountingDocumentCreditNotesExtractService;
		this.allRetryPendingFailedItemCacheFailureResolvePolicy = allRetryPendingFailedItemCacheFailureResolvePolicy;
	}

	@Override
	protected Collection<CreditNoteExtractJobItem> getItems(final List<String> ids) {
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
