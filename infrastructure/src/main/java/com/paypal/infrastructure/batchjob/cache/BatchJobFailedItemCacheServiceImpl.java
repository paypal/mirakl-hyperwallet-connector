package com.paypal.infrastructure.batchjob.cache;

import com.paypal.infrastructure.batchjob.BatchJobFailedItem;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BatchJobFailedItemCacheServiceImpl implements BatchJobFailedItemCacheService {

	private final CacheManager cacheManager;

	public BatchJobFailedItemCacheServiceImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public <T extends BatchJobItem<?>> void storeItem(T batchJobItem) {
		getCache(batchJobItem.getItemType()).put(batchJobItem.getItemId(), batchJobItem);
	}

	@Override
	public <T extends BatchJobItem<?>> Optional<T> retrieveItem(Class<T> batchJobItemClass, String batchJobItemType,
			String batchJobItemId) {
		return Optional.ofNullable(getCache(batchJobItemType).get(batchJobItemId, batchJobItemClass));
	}

	@Override
	public <T extends BatchJobItem<?>> Optional<T> retrieveItem(Class<T> batchJobItemClass,
			BatchJobFailedItem batchJobFailedItem) {
		return Optional
				.ofNullable(getCache(batchJobFailedItem.getType()).get(batchJobFailedItem.getId(), batchJobItemClass));
	}

	@Override
	public void removeItem(String batchJobItemType, String batchJobItemId) {
		getCache(batchJobItemType).evictIfPresent(batchJobItemId);
	}

	@Override
	public <T extends BatchJobItem<?>> void refreshCachedItems(Collection<T> extractedItems) {
		extractedItems.forEach(this::refreshCachedItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BatchJobItem<?>> void refreshCachedItem(T batchJobItem) {
		retrieveItem((Class<T>) batchJobItem.getClass(), batchJobItem.getItemType(), batchJobItem.getItemId())
				.ifPresent(oldItem -> storeItem(batchJobItem));
	}

	@Override
	public <T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> retrieveAllItems(Class<T> batchJobItemClass,
			List<BatchJobFailedItem> batchJobFailedItems) {
		return batchJobFailedItems.stream().collect(Collectors.toMap(Function.identity(),
				batchJobFailedItem -> retrieveItem(batchJobItemClass, batchJobFailedItem)));
	}

	@Override
	public <T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> retrieveAllItems(Class<T> batchJobItemClass,
			List<BatchJobFailedItem> batchJobFailedItems,
			BatchJobFailedItemCacheFailureResolver<T> cacheFailureResolver) {
		return retrieveAllItems(batchJobItemClass, batchJobFailedItems, cacheFailureResolver,
				cacheFailures -> cacheFailures);
	}

	@Override
	public <T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> retrieveAllItems(Class<T> batchJobItemClass,
			List<BatchJobFailedItem> batchJobFailedItems,
			BatchJobFailedItemCacheFailureResolver<T> cacheFailureResolver,
			BatchJobFailedItemCacheFailureResolvePolicy resolvePolicy) {

		Map<BatchJobFailedItem, Optional<T>> cachedItems = retrieveAllItems(batchJobItemClass, batchJobFailedItems);

		List<BatchJobFailedItem> cacheFailures = getCacheFailures(cachedItems);
		if (!cacheFailures.isEmpty()) {
			cachedItems = resolveCacheFailures(batchJobItemClass, batchJobFailedItems, cacheFailures,
					cacheFailureResolver, resolvePolicy);
		}
		return cachedItems;
	}

	private <T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> resolveCacheFailures(
			Class<T> batchJobItemClass, List<BatchJobFailedItem> requestedItems, List<BatchJobFailedItem> cacheFailures,
			BatchJobFailedItemCacheFailureResolver<T> cacheFailureResolver,
			BatchJobFailedItemCacheFailureResolvePolicy resolvePolicy) {

		List<BatchJobFailedItem> itemsToReload = resolvePolicy.itemsToReloadOnCacheFailure(cacheFailures);

		Collection<T> resolvedItems = cacheFailureResolver.itemsToBeCached(itemsToReload);
		resolvedItems.forEach(this::storeItem);

		return retrieveAllItems(batchJobItemClass, requestedItems);
	}

	private <T extends BatchJobItem<?>> List<BatchJobFailedItem> getCacheFailures(
			Map<BatchJobFailedItem, Optional<T>> resolvedItems) {
		return resolvedItems.entrySet().stream().filter(it -> it.getValue().isEmpty()).map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	private @NonNull Cache getCache(String batchJobItemType) {
		return Objects.requireNonNull(cacheManager.getCache(batchJobItemType));
	}

}
