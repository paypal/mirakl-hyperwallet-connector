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
	protected List<BatchJobProcessingListener<BatchJobContext, BatchJobItem<?>>> batchJobProcessingListeners;

	public <C extends BatchJobContext> void execute(BatchJob<C, BatchJobItem<?>> job, C ctx) {
		try {
			reportBatchJobStarted(ctx);

			retrieveBatchItems(job, ctx).forEach(i -> processItem(job, ctx, i));

			reportBatchJobFinished(ctx);
		}
		catch (final RuntimeException e) {
			reportBatchJobFailure(ctx, e);
		}
		ctx.resetCounters();
	}

	private <C extends BatchJobContext> Collection<BatchJobItem<?>> retrieveBatchItems(BatchJob<C, BatchJobItem<?>> job,
			final C context) {
		try {
			reportItemExtractionStarted(context);

			final Collection<BatchJobItem<?>> items = job.getItems(context);
			context.setNumberOfItemsToBeProcessed(items.size());

			reportItemExtractionFinished(context, items);
			return items;
		}
		catch (final RuntimeException e) {
			reportItemExtractionFailure(context, e);
			throw e;
		}
	}

	private <C extends BatchJobContext, T extends BatchJobItem<?>> void processItem(BatchJob<C, T> job, final C context,
			final T item) {
		try {
			reportItemProcessingStarted(context, item);
			job.processItem(context, item);
			reportItemProcessingFinished(context, item);
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
		ctx.setFinishedStatus();

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

	private <C extends BatchJobContext> void reportItemProcessingFailure(final C ctx, final BatchJobItem<?> item,
			final RuntimeException e) {
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

	private <C extends BatchJobContext> void reportItemProcessingFinished(final C ctx, final BatchJobItem<?> item) {
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

	private <C extends BatchJobContext> void reportItemProcessingStarted(final C ctx, final BatchJobItem<?> item) {
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

	private <C extends BatchJobContext> void reportItemExtractionFinished(final C ctx,
			Collection<BatchJobItem<?>> extractedItems) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onItemExtractionSuccessful(ctx, extractedItems);
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

}
