package com.paypal.infrastructure.service;

/**
 * Service for ensure the tokens in hypewallet and mirakl ar in sync
 *
 * @param <T>
 */
public interface TokenSynchronizationService<T> {

	/**
	 * Synchronize the tokens in hypewallet and mirakl for a specific item
	 * @param model that contains the item to synchronize
	 * @return the updated model item with the token in case there was a synchronization
	 */
	T synchronizeToken(T model);

}
