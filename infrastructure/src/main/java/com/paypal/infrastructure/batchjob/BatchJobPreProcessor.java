package com.paypal.infrastructure.batchjob;

import java.util.Collection;

/**
 * BatchJobs will use classes implementing this interface for doing preparation tasks
 * needed before start processing items.
 *
 * @param <C> the job context type.
 * @param <T> the item type.
 */
public interface BatchJobPreProcessor<C extends BatchJobContext, T extends BatchJobItem<?>> {

	/**
	 * Do preparation tasks before processing the provided items.
	 * @param ctx the batch context.
	 * @param itemsToBeProcessed the items that are going to be processed.
	 */
	void prepareForProcessing(C ctx, Collection<T> itemsToBeProcessed);

}
