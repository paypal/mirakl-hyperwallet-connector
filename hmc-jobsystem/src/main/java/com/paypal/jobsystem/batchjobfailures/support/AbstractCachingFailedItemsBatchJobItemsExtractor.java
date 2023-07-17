package com.paypal.jobsystem.batchjobfailures.support;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobfailures.services.resolvepolicies.BatchJobFailedItemCacheFailureResolvePolicy;
import com.paypal.jobsystem.batchjobfailures.services.cache.BatchJobFailedItemCacheService;
import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractCachingFailedItemsBatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		extends AbstractFailedItemsBatchJobItemsExtractor<C, T> {

	private final BatchJobFailedItemCacheService batchJobFailedItemCacheService;

	private final Class<T> batchJobItemClass;

	protected AbstractCachingFailedItemsBatchJobItemsExtractor(final Class<T> batchJobItemClass, final String itemType,
			final BatchJobFailedItemService batchJobFailedItemService,
			final BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
		super(itemType, batchJobFailedItemService);
		this.batchJobItemClass = batchJobItemClass;
		this.batchJobFailedItemCacheService = batchJobFailedItemCacheService;
	}

	protected abstract Collection<T> getItems(List<String> ids);

	protected Optional<BatchJobFailedItemCacheFailureResolvePolicy> getBatchJobFailedItemCacheFailureResolvePolicy() {
		return Optional.empty();
	}

	@Override
	protected Collection<T> getBatchJobFailedItems(final List<BatchJobFailedItem> batchJobFailedItems) {
		//@formatter:off
		return retrieveCachedItems(batchJobFailedItems)
				.values().stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
		//@formatter:on
	}

	private Map<BatchJobFailedItem, Optional<T>> retrieveCachedItems(
			final List<BatchJobFailedItem> batchJobFailedItems) {
		final Optional<BatchJobFailedItemCacheFailureResolvePolicy> policy = getBatchJobFailedItemCacheFailureResolvePolicy();

		if (policy.isEmpty()) {
			return batchJobFailedItemCacheService.retrieveAllItems(batchJobItemClass, batchJobFailedItems,
					this::getBatchJobFailedItemsInternal);
		}
		else {
			return batchJobFailedItemCacheService.retrieveAllItems(batchJobItemClass, batchJobFailedItems,
					this::getBatchJobFailedItemsInternal, policy.get());
		}
	}

	protected Collection<T> getBatchJobFailedItemsInternal(final List<BatchJobFailedItem> batchJobFailedItems) {
		return getItems(batchJobFailedItems.stream().map(BatchJobFailedItem::getId).collect(Collectors.toList()));
	}

}
