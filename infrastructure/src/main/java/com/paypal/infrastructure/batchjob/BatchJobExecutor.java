package com.paypal.infrastructure.batchjob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class BatchJobExecutor {

	public static final String MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER = "Error while invoking batch job listener";

	@Resource
	protected List<BatchJobProcessingListener> batchJobProcessingListeners;

	public <C extends BatchJobContext, T extends BatchJobItem<?>> void execute(BatchJob<C, T> job, C ctx) {
		try {
			reportBatchJobStarted(ctx);

			Collection<T> itemsToBeProcessed = retrieveBatchItems(job, ctx);

			prepareForProcessing(job, ctx, itemsToBeProcessed);

			itemsToBeProcessed.forEach(i -> processItem(job, ctx, i));

			reportBatchJobFinished(ctx);
		}
		catch (final RuntimeException e) {
			reportBatchJobFailure(ctx, e);
		}
		ctx.resetCounters();
	}

	private <C extends BatchJobContext, T extends BatchJobItem<?>> Collection<T> retrieveBatchItems(BatchJob<C, T> job,
			final C context) {
		try {
			reportItemExtractionStarted(context);

			final Collection<T> items = job.getItems(context);
			context.setNumberOfItemsToBeProcessed(items.size());

			reportItemExtractionFinished(context, items);
			return items;
		}
		catch (final RuntimeException e) {
			reportItemExtractionFailure(context, e);
			throw e;
		}
	}

	private <T extends BatchJobItem<?>, C extends BatchJobContext> void prepareForProcessing(BatchJob<C, T> job,
			C context, Collection<T> itemsToBeProcessed) {
		try {
			reportPreparationForProcessingStarted(context);

			job.prepareForItemProcessing(context, itemsToBeProcessed);

			reportPreparationForProcessingFinished(context);
		}
		catch (final RuntimeException e) {
			reportPreparationForProcessingFailure(context, e);
		}
	}

	private <C extends BatchJobContext, T extends BatchJobItem<?>> void processItem(BatchJob<C, T> job, final C context,
			final T item) {
		try {
			reportItemProcessingStarted(context, item);

			T enrichedItem = job.enrichItem(context, item);
			BatchJobItemValidationResult validationResult = job.validateItem(context, enrichedItem);
			switch (validationResult.getStatus()) {
			case INVALID:
				reportItemProcessingValidationFailure(context, item, validationResult);
				reportItemProcessingFailure(context, item, null);
				break;
			case WARNING:
				reportItemProcessingValidationFailure(context, item, validationResult);
				job.processItem(context, enrichedItem);
				reportItemProcessingFinished(context, item);
				break;
			case VALID:
				job.processItem(context, enrichedItem);
				reportItemProcessingFinished(context, item);
				break;
			}
		}
		catch (final RuntimeException e) {
			reportItemProcessingFailure(context, item, e);
		}
	}

	private <C extends BatchJobContext> void reportBatchJobStarted(final C ctx) {
		ctx.setRunningStatus();

		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onBatchJobStarted(ctx);
			}
			catch (final RuntimeException e) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e);
			}
		}
	}

	private <C extends BatchJobContext> void reportBatchJobFinished(final C ctx) {
		if (!ctx.isPartialItemExtraction() && ctx.getNumberOfItemsFailed() == 0) {
			ctx.setFinishedStatus();
		}
		else {
			ctx.setFinishedWithFailuresStatus();
		}

		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onBatchJobFinished(ctx);
			}
			catch (final RuntimeException e) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e);
			}
		}
	}

	private <C extends BatchJobContext> void reportBatchJobFailure(final C ctx, final Exception e) {
		ctx.setFailedStatus();

		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onBatchJobFailure(ctx, e);
			}
			catch (final RuntimeException e1) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e1);
			}
		}
	}

	private <C extends BatchJobContext, T extends BatchJobItem<?>> void reportItemProcessingValidationFailure(C ctx,
			T item, BatchJobItemValidationResult validationResult) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onItemProcessingValidationFailure(ctx, item, validationResult);
			}
			catch (final RuntimeException e) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e);
			}
		}
	}

	private <C extends BatchJobContext, T extends BatchJobItem<?>> void reportItemProcessingFailure(final C ctx,
			final T item, final RuntimeException e) {
		ctx.incrementFailedItems();

		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onItemProcessingFailure(ctx, item, e);
			}
			catch (final RuntimeException e1) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e1);
			}
		}
	}

	private <C extends BatchJobContext, T extends BatchJobItem<?>> void reportItemProcessingFinished(final C ctx,
			T item) {
		ctx.incrementProcessedItems();

		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onItemProcessingSuccess(ctx, item);
			}
			catch (final RuntimeException e1) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e1);
			}
		}
	}

	private <C extends BatchJobContext, T extends BatchJobItem<?>> void reportItemProcessingStarted(final C ctx,
			final T item) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.beforeProcessingItem(ctx, item);
			}
			catch (final RuntimeException e1) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e1);
			}
		}
	}

	private <C extends BatchJobContext> void reportItemExtractionStarted(final C ctx) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.beforeItemExtraction(ctx);
			}
			catch (final RuntimeException e) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <C extends BatchJobContext, T extends BatchJobItem<?>> void reportItemExtractionFinished(final C ctx,
			Collection<T> extractedItems) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onItemExtractionSuccessful(ctx,
						(Collection<BatchJobItem<?>>) extractedItems);
			}
			catch (final RuntimeException e) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e);
			}
		}
	}

	private <C extends BatchJobContext> void reportItemExtractionFailure(final C ctx, final RuntimeException e) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onItemExtractionFailure(ctx, e);
			}
			catch (final RuntimeException e1) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e1);
			}
		}
	}

	private <C extends BatchJobContext> void reportPreparationForProcessingStarted(C context) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onPreparationForProcessingStarted(context);
			}
			catch (final RuntimeException e) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e);
			}
		}

	}

	private <C extends BatchJobContext> void reportPreparationForProcessingFinished(C context) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onPreparationForProcessingFinished(context);
			}
			catch (final RuntimeException e) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e);
			}
		}
	}

	private <C extends BatchJobContext> void reportPreparationForProcessingFailure(C context, RuntimeException e) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onPreparationForProcessingFailure(context, e);
			}
			catch (final RuntimeException e1) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e1);
			}
		}

	}

}
