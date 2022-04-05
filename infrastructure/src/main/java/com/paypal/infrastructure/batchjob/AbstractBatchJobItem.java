package com.paypal.infrastructure.batchjob;

/**
 * Wrapper class for the job item type.
 *
 * @param <T> the job item type.
 */
public abstract class AbstractBatchJobItem<T> implements BatchJobItem<T> {

	private final T item;

	protected AbstractBatchJobItem(final T item) {
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
