package com.paypal.infrastructure.batchjob;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for all batch jobs. Managed the job execution main flow including batch
 * items retrieval, item processing, reporting and failure handling.
 */
@Slf4j
public abstract class AbstractBatchJob<C extends BatchJobContext, T extends BatchJobItem<?>> {

	public static final String MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER = "Error while invoking batch job listener";

	@Resource
	private List<BatchJobProcessingListener<C, T>> batchJobProcessingListeners;

	public void setBatchJobProcessingListeners(
			final List<BatchJobProcessingListener<C, T>> batchJobProcessingListeners) {
		this.batchJobProcessingListeners = batchJobProcessingListeners;
	}

	protected abstract BatchJobItemProcessor<C, T> getBatchJobItemProcessor();

	protected abstract BatchJobItemsExtractor<C, T> getBatchJobItemsExtractor();

	@SuppressWarnings("unchecked")
	protected C getBatchJobContext(final JobExecutionContext jobExecutionContext) {
		return (C) new BatchJobContext(jobExecutionContext);
	}

	/**
	 * Executes the job, extracting the items and processing them individually.
	 * @param jobExecutionContext the {@link JobExecutionContext}.
	 * @throws JobExecutionException the exception.
	 */
	public void execute(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
		final var ctx = getBatchJobContext(jobExecutionContext);
		try {
			reportBatchJobStarted(ctx);

			//@formatter:off
			Optional.ofNullable(retrieveBatchItems(ctx))
					.ifPresent(items -> items.stream()
							.filter(Objects::nonNull)
							.forEach(i -> processItem(ctx, i)));
			//@formatter:on

			reportBatchJobFinished(ctx);
		}
		catch (final RuntimeException e) {
			reportBatchJobFailure(ctx, e);
		}
		ctx.resetCounters();
	}

	private Collection<T> retrieveBatchItems(final C context) {
		try {
			reportItemExtractionStarted(context);

			final Collection<T> items = getBatchJobItemsExtractor().getItems(context);
			context.setNumberOfItemsToBeProcessed(items != null ? items.size() : 0);

			reportItemExtractionFinished(context);
			return items;
		}
		catch (final RuntimeException e) {
			reportItemExtractionFailure(context, e);
			throw e;
		}
	}

	private void processItem(final C context, final T item) {
		try {
			reportItemProcessingStarted(context, item);
			getBatchJobItemProcessor().processItem(context, item);
			reportItemProcessingFinished(context, item);
		}
		catch (final RuntimeException e) {
			reportItemProcessingFailure(context, item, e);
		}
	}

	private void reportBatchJobStarted(final C ctx) {
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

	private void reportBatchJobFinished(final C ctx) {
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

	private void reportBatchJobFailure(final C ctx, final Exception e) {
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

	private void reportItemProcessingFailure(final C ctx, final T item, final RuntimeException e) {
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

	private void reportItemProcessingFinished(final C ctx, final T item) {
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

	private void reportItemProcessingStarted(final C ctx, final T item) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.beforeProcessingItem(ctx, item);
			}
			catch (final RuntimeException e1) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e1);
			}
		}
	}

	private void reportItemExtractionStarted(final C ctx) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.beforeItemExtraction(ctx);
			}
			catch (final RuntimeException e) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e);
			}
		}
	}

	private void reportItemExtractionFinished(final C ctx) {
		for (final var batchJobProcessingListener : batchJobProcessingListeners) {
			try {
				batchJobProcessingListener.onItemExtractionSuccessful(ctx);
			}
			catch (final RuntimeException e) {
				log.error(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER, e);
			}
		}
	}

	private void reportItemExtractionFailure(final C ctx, final RuntimeException e) {
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
