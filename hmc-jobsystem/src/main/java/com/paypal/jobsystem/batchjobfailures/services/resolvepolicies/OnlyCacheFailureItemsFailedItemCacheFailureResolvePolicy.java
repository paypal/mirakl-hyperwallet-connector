package com.paypal.jobsystem.batchjobfailures.services.resolvepolicies;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
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
	public List<BatchJobFailedItem> itemsToReloadOnCacheFailureInternal(final List<BatchJobFailedItem> cacheFailures) {
		return new ArrayList<>(cacheFailures);
	}

}
