package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extract bank accounts for retry from the failed items cache.
 */
@Service
public class BankAccountRetryBatchJobItemsExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, BankAccountExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	protected BankAccountRetryBatchJobItemsExtractor(BatchJobFailedItemService batchJobFailedItemService,
			BatchJobFailedItemCacheService batchJobFailedItemCacheService,
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
