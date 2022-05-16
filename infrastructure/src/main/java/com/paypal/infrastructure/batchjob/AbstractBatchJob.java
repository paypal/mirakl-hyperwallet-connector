package com.paypal.infrastructure.batchjob;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * Base class for all batch jobs. Managed the job execution main flow including batch
 * items retrieval, item processing, reporting and failure handling.
 */
@Slf4j
public abstract class AbstractBatchJob<C extends BatchJobContext, T extends BatchJobItem<?>> implements BatchJob<C, T> {

	protected abstract BatchJobItemProcessor<C, T> getBatchJobItemProcessor();

	protected abstract BatchJobItemsExtractor<C, T> getBatchJobItemsExtractor();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<T> getItems(C ctx) {
		return getBatchJobItemsExtractor().getItems(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processItem(final C ctx, final T jobItem) {
		getBatchJobItemProcessor().processItem(ctx, jobItem);
	}

}
