package com.paypal.jobsystem.batchjobfailures.services.resolvepolicies;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;

import java.util.List;

/**
 * Cache failure resolve policy decides what items should be reloaded into a cache when a
 * cache failure happens.
 *
 * This allows to define behaviours such as load only the items that weren't found on the
 * cache or reload all items that could be potentially accessed to minimize the amount of
 * requests needed to the APIs that provide the different items values.
 */
@FunctionalInterface
public interface BatchJobFailedItemCacheFailureResolvePolicy {

	/**
	 * Returns the items that need to be reloaded into cache.
	 * @param cacheFailures Items that were not found on the fail items cache.
	 * @return The list of items that should be reloaded into the fail items cache.
	 */
	List<BatchJobFailedItem> itemsToReloadOnCacheFailure(List<BatchJobFailedItem> cacheFailures);

}
