package com.paypal.jobsystem.batchjobsupport.support;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;

/**
 * Wrapper class for the job item type.
 *
 * @param <T> the job item type.
 */
public abstract class AbstractBatchJobItem<T> implements BatchJobItem<T> {

	private final T item;

	protected AbstractBatchJobItem(final T item) {
		if (item == null) {
			throw new IllegalArgumentException("Batch job items can't be empty.");
		}
		this.item = item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getItem() {
		return item;
	}

}
