package com.paypal.infrastructure.batchjob;

/**
 * Provides handlers that will be triggered during item job execution.
 *
 * @param <C> the job context type.
 * @param <T> the job item type.
 */
public interface BatchJobItemProcessingListener<C extends BatchJobContext, T extends BatchJobItem<?>> {

	/**
	 * Handler before processing item.
	 * @param ctx the job context.
	 * @param item the item.
	 */
	void beforeProcessingItem(C ctx, T item);

	/**
	 * Handler after processing item.
	 * @param ctx the job context.
	 * @param item the item.
	 */
	void afterProcessingItem(C ctx, T item);

	/**
	 * Handler on item processing failure.
	 * @param ctx the job context.
	 * @param item the item.
	 * @param e the exception.
	 */
	void onItemProcessingFailure(C ctx, T item, RuntimeException e);

	/**
	 * Handler on item processing success.
	 * @param ctx the job context.
	 * @param item the item.
	 */
	void onItemProcessingSuccess(C ctx, T item);

}
