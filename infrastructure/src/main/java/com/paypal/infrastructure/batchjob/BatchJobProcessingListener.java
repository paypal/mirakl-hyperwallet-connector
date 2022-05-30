package com.paypal.infrastructure.batchjob;

import java.util.Collection;

/**
 * Provides handlers that will be triggered during job execution.
 */
public interface BatchJobProcessingListener {

	/**
	 * Handler before item extraction.
	 * @param ctx the job context.
	 */
	void beforeItemExtraction(BatchJobContext ctx);

	/**
	 * Handler on successful item extraction.
	 * @param ctx the job context.
	 * @param extractedItems the items extracted.
	 */
	void onItemExtractionSuccessful(BatchJobContext ctx, Collection<BatchJobItem<?>> extractedItems);

	/**
	 * Handler on item extraction failure.
	 * @param ctx the job context.
	 * @param e the exception.
	 */
	void onItemExtractionFailure(BatchJobContext ctx, Exception e);

	/**
	 * Handler before item processing.
	 * @param ctx the job cont.
	 * @param item the item.
	 */
	void beforeProcessingItem(BatchJobContext ctx, BatchJobItem<?> item);

	/**
	 * Handler on failure item processing.
	 * @param ctx the job context.
	 * @param item the item.
	 * @param e the exception.
	 */
	void onItemProcessingFailure(BatchJobContext ctx, BatchJobItem<?> item, Exception e);

	/**
	 * Handler on success item processing.
	 * @param ctx the job context.
	 * @param item the item.
	 */
	void onItemProcessingSuccess(BatchJobContext ctx, BatchJobItem<?> item);

	/**
	 * Handler on started job.
	 * @param ctx the job context.
	 */
	void onBatchJobStarted(BatchJobContext ctx);

	/**
	 * Handler on finished job.
	 * @param ctx the job context.
	 */
	void onBatchJobFinished(BatchJobContext ctx);

	/**
	 * Handler on failure job.
	 * @param ctx the job context.
	 * @param e the exception.
	 */
	void onBatchJobFailure(BatchJobContext ctx, Exception e);

}
