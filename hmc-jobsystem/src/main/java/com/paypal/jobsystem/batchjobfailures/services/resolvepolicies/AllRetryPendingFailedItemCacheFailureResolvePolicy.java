package com.paypal.jobsystem.batchjobfailures.services.resolvepolicies;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItemStatus;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Cache failure resolve policy that reloads all potential retryable items. The purpose of
 * this policy is to minimize the amount of request to retrieve items from the servers.
 */
@Component
public class AllRetryPendingFailedItemCacheFailureResolvePolicy
		extends AbstractBatchJobFailedItemCacheFailureResolvePolicy {

	private final BatchJobFailedItemService batchJobFailedItemService;

	public AllRetryPendingFailedItemCacheFailureResolvePolicy(
			final BatchJobFailedItemService batchJobFailedItemService) {
		this.batchJobFailedItemService = batchJobFailedItemService;
	}

	@Override
	public List<BatchJobFailedItem> itemsToReloadOnCacheFailureInternal(final List<BatchJobFailedItem> cacheFailures) {
		return batchJobFailedItemService.getFailedItems(getItemsType(cacheFailures),
				BatchJobFailedItemStatus.RETRY_PENDING);
	}

}
