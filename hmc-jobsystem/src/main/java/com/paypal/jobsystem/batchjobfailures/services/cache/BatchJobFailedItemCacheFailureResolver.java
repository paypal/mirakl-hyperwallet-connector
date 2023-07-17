package com.paypal.jobsystem.batchjobfailures.services.cache;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;

import java.util.Collection;
import java.util.List;

/**
 * This interface is used in {@link BatchJobFailedItemCacheService} to get the item values
 * when a cache failure happens.
 */
@FunctionalInterface
public interface BatchJobFailedItemCacheFailureResolver<T extends BatchJobItem<?>> {

	/**
	 * Return the items to be stored in the cache.
	 * @param cacheFailures List of failed items that should be reloaded in the cache.
	 * @return Items to be stored in the cache
	 */
	Collection<T> itemsToBeCached(List<BatchJobFailedItem> cacheFailures);

}
