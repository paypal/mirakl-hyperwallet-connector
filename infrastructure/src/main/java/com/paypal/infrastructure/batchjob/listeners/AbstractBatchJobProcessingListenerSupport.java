package com.paypal.infrastructure.batchjob.listeners;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobProcessingListener;

import java.util.Collection;

/**
 * Holds common functionality for batch job processing listeners.
 *
 * @param <C> the job context type.
 * @param <T> the job item type.
 */
public class AbstractBatchJobProcessingListenerSupport<C extends BatchJobContext, T extends BatchJobItem<?>>
		implements BatchJobProcessingListener<C, T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeItemExtraction(C ctx) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionSuccessful(C ctx, Collection<T> extractedItems) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionFailure(C ctx, Exception e) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeProcessingItem(C ctx, T item) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingFailure(C ctx, T item, Exception e) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingSuccess(C ctx, T item) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobStarted(C ctx) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobFinished(C ctx) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobFailure(C ctx, Exception e) {
		// empty method
	}

}
