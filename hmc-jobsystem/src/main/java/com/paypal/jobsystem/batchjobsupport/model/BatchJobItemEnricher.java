package com.paypal.jobsystem.batchjobsupport.model;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;

/**
 * BatchJobs will use classes implementing this interface for enriching items before
 * processing them.
 *
 * @param <C> the job context type.
 * @param <T> the item type.
 */
public interface BatchJobItemEnricher<C extends BatchJobContext, T extends BatchJobItem<?>> {

	/**
	 * Enrichs the information of an item.
	 * @param ctx the batch job context.
	 * @param jobItem the item to be processed.
	 * @return the enriched item.
	 */
	T enrichItem(C ctx, T jobItem);

}
