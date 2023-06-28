package com.paypal.jobsystem.batchjob.support;

import com.paypal.jobsystem.batchjob.model.BatchJobItemValidationResult;
import com.paypal.jobsystem.batchjob.model.listeners.BatchJobProcessingListener;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;

import java.util.Collection;

/**
 * Holds common functionality for batch job processing listeners.
 */
public class AbstractBatchJobProcessingListener implements BatchJobProcessingListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeItemExtraction(final BatchJobContext ctx) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionSuccessful(final BatchJobContext ctx,
			final Collection<BatchJobItem<?>> extractedItems) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionFailure(final BatchJobContext ctx, final Exception e) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeProcessingItem(final BatchJobContext ctx, final BatchJobItem<?> item) {
		// empty method
	}

	@Override
	public void onItemProcessingValidationFailure(final BatchJobContext ctx, final BatchJobItem<?> item,
			final BatchJobItemValidationResult validationResult) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingFailure(final BatchJobContext ctx, final BatchJobItem<?> item, final Exception e) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingSuccess(final BatchJobContext ctx, final BatchJobItem<?> item) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobStarted(final BatchJobContext ctx) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobFinished(final BatchJobContext ctx) {
		// empty method
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobFailure(final BatchJobContext ctx, final Exception e) {
		// empty method
	}

	@Override
	public void onPreparationForProcessingStarted(final BatchJobContext ctx) {
		// empty method
	}

	@Override
	public void onPreparationForProcessingFinished(final BatchJobContext ctx) {
		// empty method
	}

	@Override
	public void onPreparationForProcessingFailure(final BatchJobContext ctx, final RuntimeException e) {
		// empty method
	}

}
