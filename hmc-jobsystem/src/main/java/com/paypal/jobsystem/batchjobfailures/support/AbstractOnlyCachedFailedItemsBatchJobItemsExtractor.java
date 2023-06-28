package com.paypal.jobsystem.batchjobfailures.support;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobfailures.services.cache.BatchJobFailedItemCacheService;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AbstractOnlyCachedFailedItemsBatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		extends AbstractCachingFailedItemsBatchJobItemsExtractor<C, T> {

	protected AbstractOnlyCachedFailedItemsBatchJobItemsExtractor(final Class<T> batchJobItemClass,
			final String itemType, final BatchJobFailedItemService batchJobFailedItemService,
			final BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
		super(batchJobItemClass, itemType, batchJobFailedItemService, batchJobFailedItemCacheService);
	}

	@Override
	protected Collection<T> getItems(final List<String> ids) {
		return Collections.emptyList();
	}

}
