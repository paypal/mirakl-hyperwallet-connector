package com.paypal.infrastructure.batchjob;

import java.util.Collection;

/**
 * Marker interface for Batch Jobs
 */
public interface BatchJob<C extends BatchJobContext, T extends BatchJobItem<?>> {

	/**
	 * Retrieves the items for being processed in the batch job by the given batch job
	 * context.
	 * @param ctx the batch job context.
	 * @return the items for being processed in the batch job by the given batch job
	 * context.
	 */
	Collection<T> getItems(C ctx);

	void prepareForItemProcessing(C ctx, Collection<T> itemsToBeProcessed);

	T enrichItem(C ctx, final T jobItem);

	BatchJobItemValidationResult validateItem(final C ctx, final T jobItem);

	/**
	 * Process an item with the given context.
	 * @param ctx the batch job context.
	 * @param jobItem the batch job item.
	 */
	void processItem(final C ctx, final T jobItem);

	/**
	 * Return the type of this job
	 * @return a {@link BatchJobType}+
	 */
	BatchJobType getType();

}
