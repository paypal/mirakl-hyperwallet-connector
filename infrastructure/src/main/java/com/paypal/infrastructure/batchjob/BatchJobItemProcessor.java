package com.paypal.infrastructure.batchjob;

/**
 * Exposes common functionality for extract batch job item processor.
 */
public interface BatchJobItemProcessor<C extends BatchJobContext, T extends BatchJobItem<?>> {

	/**
	 * Processes the job item.
	 * @param ctx the {@link BatchJobContext}.
	 * @param jobItem the {@link BatchJobItem}.
	 */
	void processItem(final C ctx, final T jobItem);

}
