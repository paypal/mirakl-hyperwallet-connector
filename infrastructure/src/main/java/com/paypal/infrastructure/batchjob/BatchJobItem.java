package com.paypal.infrastructure.batchjob;

/**
 * Exposes the information about a job item.
 *
 * @param <T> the job item type.
 */
public interface BatchJobItem<T> {

	/**
	 * Returns the item id.
	 * @return the item id.
	 */
	String getItemId();

	/**
	 * Returns the item type.
	 * @return the item type.
	 */
	String getItemType();

	/**
	 * Returns the item.
	 * @return the item.
	 */
	T getItem();

}
