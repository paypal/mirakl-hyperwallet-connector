package com.paypal.infrastructure.batchjob;

import java.util.List;

/**
 * Batch job failed item service.
 */
public interface BatchJobFailedItemService {

	/**
	 * Max number of attempts allows for an automatic retry job.
	 */
	int MAX_ATTEMPTS = 5;

	/**
	 * Save the item failed.
	 * @param item the item failed.
	 */
	void saveItemFailed(final BatchJobItem<?> item);

	/**
	 * Remove the item processed from failed items if exists.
	 * @param item the processed item.
	 */
	void removeItemProcessed(final BatchJobItem<?> item);

	/**
	 * Retrieves all failed items for the given item type that should be retried.
	 * @param itemType the item type.
	 * @return a {@link List} of {@link BatchJobFailedItem} for the given itemType.
	 */
	List<BatchJobFailedItem> getFailedItemsForRetry(String itemType);

	/**
	 * Retrieves all failed items for the given type
	 * @param itemType the item type.
	 * @return a {@link List} of {@link BatchJobFailedItem} for the given itemType.
	 */
	List<BatchJobFailedItem> getFailedItems(String itemType);

}
