package com.paypal.infrastructure.batchjob.listeners;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Logging batch job item processing listener.
 */
@Slf4j
@Component
public class LoggingBatchJobItemProcessingListener extends AbstractBatchJobProcessingListenerSupport {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeItemExtraction(BatchJobContext ctx) {
		log.info("[{}] Starting extraction of items to be processed", ctx.getJobName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionSuccessful(BatchJobContext ctx, Collection<BatchJobItem<?>> extractedItems) {
		log.info("[{}] Retrieved the following number of items to be processed: {}", ctx.getJobName(),
				ctx.getNumberOfItemsToBeProcessed());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionFailure(BatchJobContext ctx, Exception e) {
		log.error("[%s] Failed retrieval of items".formatted(ctx.getJobName()), e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeProcessingItem(BatchJobContext ctx, BatchJobItem<?> item) {
		log.info("[{}] Processing item of type {} with id: {}", ctx.getJobName(), item.getItemType(), item.getItemId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingFailure(BatchJobContext ctx, BatchJobItem<?> item, Exception e) {
		log.error("[%s] Failed processing item of type %s with id: %s".formatted(ctx.getJobName(), item.getItemType(),
				item.getItemId()), e);
		logBatchProgress(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingSuccess(BatchJobContext ctx, BatchJobItem<?> item) {
		log.info("[{}] Processed successfully item of type {} with id: {}", ctx.getJobName(), item.getItemType(),
				item.getItemId());
		logBatchProgress(ctx);
	}

	private void logBatchProgress(BatchJobContext ctx) {
		log.info("[{}] {} items processed successfully. {} items failed. {} items remaining", ctx.getJobName(),
				ctx.getNumberOfItemsProcessed(), ctx.getNumberOfItemsFailed(), ctx.getNumberOfItemsRemaining());
	}

}
