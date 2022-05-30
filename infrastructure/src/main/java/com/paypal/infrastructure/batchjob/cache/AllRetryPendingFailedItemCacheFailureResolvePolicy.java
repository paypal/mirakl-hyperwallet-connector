package com.paypal.infrastructure.batchjob.cache;

import com.paypal.infrastructure.batchjob.BatchJobFailedItem;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemStatus;
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

	public AllRetryPendingFailedItemCacheFailureResolvePolicy(BatchJobFailedItemService batchJobFailedItemService) {
		this.batchJobFailedItemService = batchJobFailedItemService;
	}

	@Override
	public List<BatchJobFailedItem> itemsToReloadOnCacheFailureInternal(List<BatchJobFailedItem> cacheFailures) {
		return batchJobFailedItemService.getFailedItems(getItemsType(cacheFailures),
				BatchJobFailedItemStatus.RETRY_PENDING);
	}

}
