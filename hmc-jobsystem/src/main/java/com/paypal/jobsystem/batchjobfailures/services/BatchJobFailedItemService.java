package com.paypal.jobsystem.batchjobfailures.services;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItemStatus;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;

import java.util.Collection;
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
	<T extends BatchJobItem<?>> void saveItemFailed(final T item);

	/**
	 * Remove the item processed from failed items if exists.
	 * @param item the processed item.
	 */
	<T extends BatchJobItem<?>> void removeItemProcessed(final T item);

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

	/**
	 * Retrieves all failed items for the given type in the given status.
	 * @param itemType the item type.
	 * @param status the status of the item.
	 * @return a {@link List} of {@link BatchJobFailedItem} for the given itemType.
	 */
	List<BatchJobFailedItem> getFailedItems(String itemType, BatchJobFailedItemStatus status);

	/**
	 * Notifies the service that the given items has been extracted, and it should review
	 * the state of any {@link BatchJobFailedItem}s associated to the given items.
	 * @param extractedItems Extracted items that should be checked.
	 */
	<T extends BatchJobItem<?>> void checkUpdatedFailedItems(Collection<T> extractedItems);

}
