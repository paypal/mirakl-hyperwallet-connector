package com.paypal.infrastructure.batchjob;

/**
 * Provides handlers that will be triggered during job execution.
 *
 * @param <C> the job context type.
 * @param <T> the job item type.
 */
public interface BatchJobProcessingListener<C extends BatchJobContext, T extends BatchJobItem<?>> {

	/**
	 * Handler before item extraction.
	 * @param ctx the job context.
	 */
	void beforeItemExtraction(C ctx);

	/**
	 * Handler on successful item extraction.
	 * @param ctx the job context.
	 */
	void onItemExtractionSuccessful(C ctx);

	/**
	 * Handler on item extraction failure.
	 * @param ctx the job context.
	 * @param e the exception.
	 */
	void onItemExtractionFailure(C ctx, Exception e);

	/**
	 * Handler before item processing.
	 * @param ctx the job cont.
	 * @param item the item.
	 */
	void beforeProcessingItem(C ctx, T item);

	/**
	 * Handler on failure item processing.
	 * @param ctx the job context.
	 * @param item the item.
	 * @param e the exception.
	 */
	void onItemProcessingFailure(C ctx, T item, Exception e);

	/**
	 * Handler on success item processing.
	 * @param ctx the job context.
	 * @param item the item.
	 */
	void onItemProcessingSuccess(C ctx, T item);

	/**
	 * Handler on started job.
	 * @param ctx the job context.
	 */
	void onBatchJobStarted(C ctx);

	/**
	 * Handler on finished job.
	 * @param ctx the job context.
	 */
	void onBatchJobFinished(C ctx);

	/**
	 * Handler on failure job.
	 * @param ctx the job context.
	 * @param e the exception.
	 */
	void onBatchJobFailure(C ctx, Exception e);

}
