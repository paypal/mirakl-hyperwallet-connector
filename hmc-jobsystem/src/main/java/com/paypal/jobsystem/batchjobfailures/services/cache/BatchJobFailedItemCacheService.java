package com.paypal.jobsystem.batchjobfailures.services.cache;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobfailures.services.resolvepolicies.BatchJobFailedItemCacheFailureResolvePolicy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This service stores failed batch job items in memory, so they can be used by retry
 * batch jobs without needing to get the items again from the HTTP endpoints.
 *
 * This catch only stores the failed item {@link BatchJobItem} and not the information
 * about the failure {@link BatchJobFailedItem}
 */
@Service
public interface BatchJobFailedItemCacheService {

	/**
	 * Stores an item in the cache.
	 * @param batchJobItem The item to be cached
	 */
	<T extends BatchJobItem<?>> void storeItem(T batchJobItem);

	/**
	 * Retrieves an item from the cache.
	 * @param batchJobItemType Type of the cached item.
	 * @param batchJobItemId Id of the cached item.
	 * @param <T> Type of the wrapped object in the item.
	 * @return The cached item.
	 */
	<T extends BatchJobItem<?>> Optional<T> retrieveItem(Class<T> batchJobItemClass, String batchJobItemType,
			String batchJobItemId);

	/**
	 * Retrieves an item from the cache.
	 * @param batchJobFailedItem Information about the failed item to be retrieved.
	 * @param <T> Type of the wrapped object in the item.
	 * @return The cached item.
	 */
	<T extends BatchJobItem<?>> Optional<T> retrieveItem(Class<T> batchJobItemClass,
			BatchJobFailedItem batchJobFailedItem);

	/**
	 * Removes an item from the cache.
	 * @param batchJobItemType Type of the cached item.
	 * @param batchJobItemId Id of the cached item.
	 */
	void removeItem(String batchJobItemType, String batchJobItemId);

	/**
	 * Updates an item in the cache only if it's found in the cache. If it's not found
	 * it's not added to the cache.
	 * @param batchJobItem Item to be updated.
	 */
	<T extends BatchJobItem<?>> void refreshCachedItem(T batchJobItem);

	/**
	 * Updates a collection of items in the cache if they are found in the cache. The
	 * items that are not in the cache are not added.
	 * @param batchJobItems Items to be updated.
	 */
	<T extends BatchJobItem<?>> void refreshCachedItems(Collection<T> batchJobItems);

	/**
	 * Retrieves all requested items from the cache. If they are not found and empty
	 * {@link Optional} is returned to the response.
	 * @param batchJobFailedItems List of information about the failed items that should
	 * be retrieved from the cache.
	 * @param <T> Type of the wrapped object in the item.
	 * @return A map containing for each requested item an {@link Optional} with the
	 * cached item.
	 */
	<T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> retrieveAllItems(Class<T> batchJobItemClass,
			List<BatchJobFailedItem> batchJobFailedItems);

	/**
	 * Retrieves all requested items from the cache. The items that are not found will be
	 * tried to be obtained and loaded into the cache using the provided
	 * {@link BatchJobFailedItemCacheFailureResolver}.
	 * @param batchJobFailedItems List of information about the failed items that should
	 * be retrieved from the cache.
	 * @param cacheFailureResolver Resolver to obtain the {@link BatchJobItem} from the
	 * information contained in {@link BatchJobFailedItem}
	 * @param <T> Type of the wrapped object in the item.
	 * @return A map containing for each requested item an {@link Optional} with the
	 * cached item after the resolution process was executed.
	 */
	<T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> retrieveAllItems(Class<T> batchJobItemClass,
			List<BatchJobFailedItem> batchJobFailedItems,
			BatchJobFailedItemCacheFailureResolver<T> cacheFailureResolver);

	/**
	 * Retrieves all requested items from the cache. The items that are not found will be
	 * tried to be obtained and loaded into the cache using the provided
	 * {@link BatchJobFailedItemCacheFailureResolver}.
	 *
	 * A {@link BatchJobFailedItemCacheFailureResolvePolicy} must be provided to decide
	 * what items should be reloaded into the cache. This is useful for reloading failed
	 * items in addition to the ones that weren't found on the cache.
	 * @param batchJobFailedItems List of information about the failed items that should
	 * be retrieved from the cache.
	 * @param cacheFailureResolver Resolver to obtain the {@link BatchJobItem} from the
	 * information contained in {@link BatchJobFailedItem}
	 * @param resolvePolicy Policy to decide what failed items should be reloaded into the
	 * cache.
	 * @param <T> Type of the wrapped object in the item.
	 * @return A map containing for each requested item an {@link Optional} with the
	 * cached item after the resolution process was executed.
	 */
	<T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> retrieveAllItems(Class<T> batchJobItemClass,
			List<BatchJobFailedItem> batchJobFailedItems,
			BatchJobFailedItemCacheFailureResolver<T> cacheFailureResolver,
			BatchJobFailedItemCacheFailureResolvePolicy resolvePolicy);

}
