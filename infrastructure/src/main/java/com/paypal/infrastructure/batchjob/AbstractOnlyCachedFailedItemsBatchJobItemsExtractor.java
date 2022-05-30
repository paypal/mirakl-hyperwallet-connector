package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AbstractOnlyCachedFailedItemsBatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<C, T> {

	protected AbstractOnlyCachedFailedItemsBatchJobItemsExtractor(Class<T> batchJobItemClass, String itemType,
			BatchJobFailedItemService batchJobFailedItemService,
			BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
		super(batchJobItemClass, itemType, batchJobFailedItemService, batchJobFailedItemCacheService);
	}

	@Override
	protected Collection<T> getItems(List<String> ids) {
		return Collections.emptyList();
	}

}
