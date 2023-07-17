package com.paypal.jobsystem.batchjobfailures.services.resolvepolicies;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.infrastructure.support.exceptions.HMCException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractBatchJobFailedItemCacheFailureResolvePolicy
		implements BatchJobFailedItemCacheFailureResolvePolicy {

	@Override
	public List<BatchJobFailedItem> itemsToReloadOnCacheFailure(final List<BatchJobFailedItem> cacheFailures) {
		if (cacheFailures.isEmpty()) {
			return Collections.emptyList();
		}
		checkRestrictions(cacheFailures);
		return itemsToReloadOnCacheFailureInternal(cacheFailures);
	}

	protected void checkRestrictions(final List<BatchJobFailedItem> cacheFailures) {
		final Set<String> itemTypes = cacheFailures.stream().map(BatchJobFailedItem::getType)
				.collect(Collectors.toSet());
		if (itemTypes.size() > 1) {
			throw new HMCException("More than one item type has been passed to cache failure resolver.");
		}
	}

	protected abstract List<BatchJobFailedItem> itemsToReloadOnCacheFailureInternal(
			List<BatchJobFailedItem> cacheFailures);

	protected String getItemsType(final List<BatchJobFailedItem> cacheFailures) {
		return cacheFailures.stream().map(BatchJobFailedItem::getType).findFirst()
				.orElseThrow(() -> new HMCException("Unknown item types passed to cache failure resolver."));
	}

}
