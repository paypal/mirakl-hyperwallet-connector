package com.paypal.jobsystem.batchjobaudit.listeners;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.jobsystem.batchjob.support.AbstractBatchJobProcessingListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Tracking batch job processing listener.
 */
@Component
public class TrackingBatchJobItemProcessingListener extends AbstractBatchJobProcessingListener {

	private final BatchJobTrackingService batchJobTrackingService;

	public TrackingBatchJobItemProcessingListener(final BatchJobTrackingService batchJobTrackingService) {
		this.batchJobTrackingService = batchJobTrackingService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeItemExtraction(final BatchJobContext ctx) {
		callSuperBeforeItemExtraction(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionSuccessful(final BatchJobContext ctx,
			final Collection<BatchJobItem<?>> extractedItems) {
		batchJobTrackingService.trackJobItemsAdded(ctx.getJobUuid(), extractedItems);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionFailure(final BatchJobContext ctx, final Exception e) {
		callSuperOnItemExtractionFailure(ctx, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeProcessingItem(final BatchJobContext ctx, final BatchJobItem<?> item) {
		batchJobTrackingService.trackJobItemProcessingStarted(ctx.getJobUuid(), item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingFailure(final BatchJobContext ctx, final BatchJobItem<?> item, final Exception e) {
		batchJobTrackingService.trackJobItemProcessingFinished(ctx.getJobUuid(), item, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingSuccess(final BatchJobContext ctx, final BatchJobItem<?> item) {
		batchJobTrackingService.trackJobItemProcessingFinished(ctx.getJobUuid(), item, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobStarted(final BatchJobContext ctx) {
		batchJobTrackingService.markNonFinishedJobsAsAborted(ctx.getJobName());
		batchJobTrackingService.trackJobStart(ctx.getJobUuid(), ctx.getJobName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobFinished(final BatchJobContext ctx) {
		batchJobTrackingService.trackJobFinished(ctx.getJobUuid(),
				ctx.getNumberOfItemsFailed() == 0 && !ctx.isPartialItemExtraction());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobFailure(final BatchJobContext ctx, final Exception e) {
		batchJobTrackingService.trackJobFailure(ctx.getJobUuid(), ctx.getJobName());
	}

	protected void callSuperBeforeItemExtraction(final BatchJobContext ctx) {
		super.beforeItemExtraction(ctx);
	}

	protected void callSuperOnItemExtractionFailure(final BatchJobContext ctx, final Exception e) {
		super.onItemExtractionFailure(ctx, e);
	}

}
