package com.paypal.infrastructure.batchjob;

import java.util.Collection;

/**
 * Exposes common functionality for all classes that retrieves items to be processed by a
 * job.
 *
 * @param <C> the job context type.
 * @param <T> the job item type.
 */
public interface BatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>> {

	Collection<T> getItems(C ctx);

}
