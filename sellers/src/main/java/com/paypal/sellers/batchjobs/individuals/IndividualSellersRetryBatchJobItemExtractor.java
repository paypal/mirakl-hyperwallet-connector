package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.AbstractCachingFailedItemsBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndividualSellersRetryBatchJobItemExtractor
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, IndividualSellersExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	public IndividualSellersRetryBatchJobItemExtractor(final MiraklSellersExtractService miraklSellersExtractService,
			final BatchJobFailedItemService batchJobFailedItemService,
			BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
		super(IndividualSellersExtractJobItem.class, IndividualSellersExtractJobItem.ITEM_TYPE,
				batchJobFailedItemService, batchJobFailedItemCacheService);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	@Override
	protected Collection<IndividualSellersExtractJobItem> getItems(List<String> ids) {
		return miraklSellersExtractService.extractSellers(ids).stream().map(IndividualSellersExtractJobItem::new)
				.collect(Collectors.toList());
	}

}
