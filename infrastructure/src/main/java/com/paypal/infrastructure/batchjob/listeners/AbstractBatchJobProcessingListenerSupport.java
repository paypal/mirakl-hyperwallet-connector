package com.paypal.infrastructure.batchjob.listeners;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobItemValidationResult;
import com.paypal.infrastructure.batchjob.BatchJobProcessingListener;

import java.util.Collection;

/**
 * Holds common functionality for batch job processing listeners.
 */
public class AbstractBatchJobProcessingListenerSupport implements BatchJobProcessingListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeItemExtraction(BatchJobContext ctx) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionSuccessful(BatchJobContext ctx, Collection<BatchJobItem<?>> extractedItems) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionFailure(BatchJobContext ctx, Exception e) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeProcessingItem(BatchJobContext ctx, BatchJobItem<?> item) {
		// empty method
	}

	@Override
	public void onItemProcessingValidationFailure(BatchJobContext ctx, BatchJobItem<?> item,
			BatchJobItemValidationResult validationResult) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingFailure(BatchJobContext ctx, BatchJobItem<?> item, Exception e) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingSuccess(BatchJobContext ctx, BatchJobItem<?> item) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobStarted(BatchJobContext ctx) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobFinished(BatchJobContext ctx) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobFailure(BatchJobContext ctx, Exception e) {
		// empty method
	}

	@Override
	public void onPreparationForProcessingStarted(BatchJobContext ctx) {
		// empty method
	}

	@Override
	public void onPreparationForProcessingFinished(BatchJobContext ctx) {
		// empty method
	}

	@Override
	public void onPreparationForProcessingFailure(BatchJobContext ctx, RuntimeException e) {
		// empty method
	}

}
