package com.paypal.infrastructure.batchjob.cache;

import com.paypal.infrastructure.batchjob.BatchJobFailedItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Cache failure resolve policy that simply select to the reload only the items that
 * weren't found on the cache.
 */
@Component
public class OnlyCacheFailureItemsFailedItemCacheFailureResolvePolicy
		extends AbstractBatchJobFailedItemCacheFailureResolvePolicy {

	@Override
	public List<BatchJobFailedItem> itemsToReloadOnCacheFailureInternal(List<BatchJobFailedItem> cacheFailures) {
		return new ArrayList<>(cacheFailures);
	}

}
