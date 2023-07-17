package com.paypal.sellers.bankaccountextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjobfailures.services.cache.BatchJobFailedItemCacheService;
import com.paypal.jobsystem.batchjobfailures.support.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extract bank accounts for retry from the failed items cache.
 */
@Component
public class BankAccountRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, BankAccountExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	protected BankAccountRetryBatchJobItemsExtractor(final BatchJobFailedItemService batchJobFailedItemService,
			final BatchJobFailedItemCacheService batchJobFailedItemCacheService,
			final MiraklSellersExtractService miraklSellersExtractService) {
		super(BankAccountExtractJobItem.class, BankAccountExtractJobItem.ITEM_TYPE, batchJobFailedItemService,
				batchJobFailedItemCacheService);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	@Override
	protected Collection<BankAccountExtractJobItem> getItems(final List<String> ids) {

		//@formatter:off
		return miraklSellersExtractService.extractSellers(ids)
				.stream()
				.map(BankAccountExtractJobItem::new)
				.collect(Collectors.toList());
		//@formatter:on
	}

}
