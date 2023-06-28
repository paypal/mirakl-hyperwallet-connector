package com.paypal.jobsystem.batchjobfailures.services.cache;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobfailures.services.resolvepolicies.BatchJobFailedItemCacheFailureResolvePolicy;
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

	public BatchJobFailedItemCacheServiceImpl(final CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public <T extends BatchJobItem<?>> void storeItem(final T batchJobItem) {
		getCache(batchJobItem.getItemType()).put(batchJobItem.getItemId(), batchJobItem);
	}

	@Override
	public <T extends BatchJobItem<?>> Optional<T> retrieveItem(final Class<T> batchJobItemClass,
			final String batchJobItemType, final String batchJobItemId) {
		return Optional.ofNullable(getCache(batchJobItemType).get(batchJobItemId, batchJobItemClass));
	}

	@Override
	public <T extends BatchJobItem<?>> Optional<T> retrieveItem(final Class<T> batchJobItemClass,
			final BatchJobFailedItem batchJobFailedItem) {
		return Optional
				.ofNullable(getCache(batchJobFailedItem.getType()).get(batchJobFailedItem.getId(), batchJobItemClass));
	}

	@Override
	public void removeItem(final String batchJobItemType, final String batchJobItemId) {
		getCache(batchJobItemType).evictIfPresent(batchJobItemId);
	}

	@Override
	public <T extends BatchJobItem<?>> void refreshCachedItems(final Collection<T> extractedItems) {
		extractedItems.forEach(this::refreshCachedItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BatchJobItem<?>> void refreshCachedItem(final T batchJobItem) {
		retrieveItem((Class<T>) batchJobItem.getClass(), batchJobItem.getItemType(), batchJobItem.getItemId())
				.ifPresent(oldItem -> storeItem(batchJobItem));
	}

	@Override
	public <T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> retrieveAllItems(
			final Class<T> batchJobItemClass, final List<BatchJobFailedItem> batchJobFailedItems) {
		return batchJobFailedItems.stream().collect(Collectors.toMap(Function.identity(),
				batchJobFailedItem -> retrieveItem(batchJobItemClass, batchJobFailedItem)));
	}

	@Override
	public <T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> retrieveAllItems(
			final Class<T> batchJobItemClass, final List<BatchJobFailedItem> batchJobFailedItems,
			final BatchJobFailedItemCacheFailureResolver<T> cacheFailureResolver) {
		return retrieveAllItems(batchJobItemClass, batchJobFailedItems, cacheFailureResolver,
				cacheFailures -> cacheFailures);
	}

	@Override
	public <T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> retrieveAllItems(
			final Class<T> batchJobItemClass, final List<BatchJobFailedItem> batchJobFailedItems,
			final BatchJobFailedItemCacheFailureResolver<T> cacheFailureResolver,
			final BatchJobFailedItemCacheFailureResolvePolicy resolvePolicy) {

		Map<BatchJobFailedItem, Optional<T>> cachedItems = retrieveAllItems(batchJobItemClass, batchJobFailedItems);

		final List<BatchJobFailedItem> cacheFailures = getCacheFailures(cachedItems);
		if (!cacheFailures.isEmpty()) {
			cachedItems = resolveCacheFailures(batchJobItemClass, batchJobFailedItems, cacheFailures,
					cacheFailureResolver, resolvePolicy);
		}
		return cachedItems;
	}

	private <T extends BatchJobItem<?>> Map<BatchJobFailedItem, Optional<T>> resolveCacheFailures(
			final Class<T> batchJobItemClass, final List<BatchJobFailedItem> requestedItems,
			final List<BatchJobFailedItem> cacheFailures,
			final BatchJobFailedItemCacheFailureResolver<T> cacheFailureResolver,
			final BatchJobFailedItemCacheFailureResolvePolicy resolvePolicy) {

		final List<BatchJobFailedItem> itemsToReload = resolvePolicy.itemsToReloadOnCacheFailure(cacheFailures);

		final Collection<T> resolvedItems = cacheFailureResolver.itemsToBeCached(itemsToReload);
		resolvedItems.forEach(this::storeItem);

		return retrieveAllItems(batchJobItemClass, requestedItems);
	}

	private <T extends BatchJobItem<?>> List<BatchJobFailedItem> getCacheFailures(
			final Map<BatchJobFailedItem, Optional<T>> resolvedItems) {
		return resolvedItems.entrySet().stream().filter(it -> it.getValue().isEmpty()).map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	private @NonNull Cache getCache(final String batchJobItemType) {
		return Objects.requireNonNull(cacheManager.getCache(batchJobItemType));
	}

}
