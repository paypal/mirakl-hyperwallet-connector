package com.paypal.infrastructure.batchjob;

import java.util.Collection;
import java.util.List;

/**
 * Base class for failed items extraction.
 *
 * @param <C> the job context type.
 * @param <T> the job item type.
 */
public abstract class AbstractFailedItemsBatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		implements BatchJobItemsExtractor<C, T> {

	private final String itemType;

	private final BatchJobFailedItemService batchJobFailedItemService;

	protected AbstractFailedItemsBatchJobItemsExtractor(final String itemType,
			final BatchJobFailedItemService batchJobFailedItemService) {
		this.itemType = itemType;
		this.batchJobFailedItemService = batchJobFailedItemService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<T> getItems(final C ctx) {
		return getBatchJobFailedItems(batchJobFailedItemService.getFailedItemsForRetry(itemType));
	}

	protected abstract Collection<T> getBatchJobFailedItems(List<BatchJobFailedItem> batchJobFailedItems);

	public String getItemType() {
		return itemType;
	}

}
