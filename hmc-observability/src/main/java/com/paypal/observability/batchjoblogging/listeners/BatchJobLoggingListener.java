package com.paypal.observability.batchjoblogging.listeners;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItemValidationResult;
import com.paypal.jobsystem.batchjob.support.AbstractBatchJobProcessingListener;
import com.paypal.observability.batchjoblogging.service.BatchJobLoggingContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Logging batch job item processing listener.
 */
@Slf4j
@Component
public class BatchJobLoggingListener extends AbstractBatchJobProcessingListener {

	private final BatchJobLoggingContextService batchJobLoggingContextService;

	public BatchJobLoggingListener(final BatchJobLoggingContextService batchJobLoggingContextService) {
		this.batchJobLoggingContextService = batchJobLoggingContextService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeItemExtraction(final BatchJobContext ctx) {
		log.info("Starting extraction of items to be processed");
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("java:S3655")
	@Override
	public void onItemExtractionSuccessful(final BatchJobContext ctx,
			final Collection<BatchJobItem<?>> extractedItems) {
		if (ctx.isPartialItemExtraction() && ctx.getNumberOfItemsNotSuccessfullyExtracted().isEmpty()) {
			log.warn(
					"Some of the items to be processed couldn't be retrieved. "
							+ "Only the following number of items were retrieved and are going to be processed {}",
					ctx.getNumberOfItemsToBeProcessed());
		}
		else if (ctx.isPartialItemExtraction() && ctx.getNumberOfItemsNotSuccessfullyExtracted().isPresent()) {
			log.warn(
					"Retrieved the following number of items to be processed: {}. "
							+ "Additionally there are {} items that couldn't be retrieved and can't be processed",
					ctx.getNumberOfItemsToBeProcessed(), ctx.getNumberOfItemsNotSuccessfullyExtracted().get());
		}
		else {
			log.info("Retrieved the following number of items to be processed: {}",
					ctx.getNumberOfItemsToBeProcessed());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionFailure(final BatchJobContext ctx, final Exception e) {
		log.error("Failed retrieval of items", e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeProcessingItem(final BatchJobContext ctx, final BatchJobItem<?> item) {
		batchJobLoggingContextService.refreshBatchJobInformation(ctx, item);
		log.info("Processing item of type {} with id: {}", item.getItemType(), item.getItemId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingFailure(final BatchJobContext ctx, final BatchJobItem<?> item, final Exception e) {
		log.error("Failed processing item of type %s with id: %s".formatted(item.getItemType(), item.getItemId()), e);
		logBatchProgress(ctx);
		batchJobLoggingContextService.removeBatchJobItemInformation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingSuccess(final BatchJobContext ctx, final BatchJobItem<?> item) {
		log.info("Processed successfully item of type {} with id: {}", item.getItemType(), item.getItemId());
		logBatchProgress(ctx);
		batchJobLoggingContextService.removeBatchJobItemInformation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingValidationFailure(final BatchJobContext ctx, final BatchJobItem<?> item,
			final BatchJobItemValidationResult validationResult) {
		log.warn("Validation of item of type {} with id: {} has failed with the following message: {}",
				item.getItemType(), item.getItemId(), validationResult.getReason().orElse(""));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBatchJobStarted(final BatchJobContext ctx) {
		batchJobLoggingContextService.refreshBatchJobInformation(ctx);
		log.info("Starting processing of job");
	}

	@Override
	public void onBatchJobFinished(final BatchJobContext ctx) {
		log.info("Finished processing of job");
		batchJobLoggingContextService.removeBatchJobInformation();
	}

	@Override
	public void onBatchJobFailure(final BatchJobContext ctx, final Exception e) {
		log.error("Job failed", e);
		batchJobLoggingContextService.removeBatchJobInformation();
	}

	@SuppressWarnings("java:S3655")
	private void logBatchProgress(final BatchJobContext ctx) {
		log.info("{} items processed successfully. {} items failed. {} items remaining",
				ctx.getNumberOfItemsProcessed(), ctx.getNumberOfItemsFailed(), ctx.getNumberOfItemsRemaining());
		if (ctx.getNumberOfItemsRemaining() == 0 && ctx.isPartialItemExtraction()
				&& ctx.getNumberOfItemsNotSuccessfullyExtracted().isEmpty()) {
			log.warn("Not all items were able to be retrieved during the extraction phase,"
					+ " so there are additional items that couldn't be processed since they weren't retrieved.");
		}
		else if (ctx.getNumberOfItemsRemaining() == 0 && ctx.isPartialItemExtraction()
				&& ctx.getNumberOfItemsNotSuccessfullyExtracted().isPresent()) {
			log.warn("Additionally there were {} items that couldn't be retrieved during the extraction phase,"
					+ " so they were not processed.", ctx.getNumberOfItemsNotSuccessfullyExtracted().get());
		}
	}

}
