package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheFailureResolvePolicy;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractCachingFailedItemsBatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		extends AbstractFailedItemsBatchJobItemsExtractor<C, T> {

	private final BatchJobFailedItemCacheService batchJobFailedItemCacheService;

	private final Class<T> batchJobItemClass;

	protected AbstractCachingFailedItemsBatchJobItemsExtractor(Class<T> batchJobItemClass, String itemType,
			BatchJobFailedItemService batchJobFailedItemService,
			BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
		super(itemType, batchJobFailedItemService);
		this.batchJobItemClass = batchJobItemClass;
		this.batchJobFailedItemCacheService = batchJobFailedItemCacheService;
	}

	protected abstract Collection<T> getItems(List<String> ids);

	protected Optional<BatchJobFailedItemCacheFailureResolvePolicy> getBatchJobFailedItemCacheFailureResolvePolicy() {
		return Optional.empty();
	}

	@Override
	protected Collection<T> getBatchJobFailedItems(List<BatchJobFailedItem> batchJobFailedItems) {
		//@formatter:off
		return retrieveCachedItems(batchJobFailedItems)
				.values().stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
		//@formatter:on
	}

	private Map<BatchJobFailedItem, Optional<T>> retrieveCachedItems(List<BatchJobFailedItem> batchJobFailedItems) {
		Optional<BatchJobFailedItemCacheFailureResolvePolicy> policy = getBatchJobFailedItemCacheFailureResolvePolicy();

		if (policy.isEmpty()) {
			return batchJobFailedItemCacheService.retrieveAllItems(batchJobItemClass, batchJobFailedItems,
					this::getBatchJobFailedItemsInternal);
		}
		else {
			return batchJobFailedItemCacheService.retrieveAllItems(batchJobItemClass, batchJobFailedItems,
					this::getBatchJobFailedItemsInternal, policy.get());
		}
	}

	protected Collection<T> getBatchJobFailedItemsInternal(List<BatchJobFailedItem> batchJobFailedItems) {
		return getItems(batchJobFailedItems.stream().map(BatchJobFailedItem::getId).collect(Collectors.toList()));
	}

}
