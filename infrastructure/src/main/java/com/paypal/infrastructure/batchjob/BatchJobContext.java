package com.paypal.infrastructure.batchjob;

import org.quartz.JobExecutionContext;

import java.util.Optional;

/**
 * Holds batch job context info.
 */
public class BatchJobContext {

	private static final String KEY_BATCH_JOB_STATUS = "batchJobStatus";

	private static final String KEY_NUMBER_OF_ITEMS_PROCESSED = "numberOfItemsProcessed";

	private static final String KEY_NUMBER_OF_ITEMS_FAILED = "numberOfItemsFailed";

	private static final String KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED = "numberOfItemsToBeProcessed";

	private final JobExecutionContext jobExecutionContext;

	public BatchJobContext(final JobExecutionContext jobExecutionContext) {
		this.jobExecutionContext = jobExecutionContext;
	}

	/**
	 * Returns the job class name.
	 * @return the job name.
	 */
	public String getJobName() {
		return jobExecutionContext.getJobDetail().getJobClass().getSimpleName();
	}

	/**
	 * Sets the number of items to be processed.
	 * @param numberOfItemsToBeProcessed the number of items to be processed.
	 */
	public void setNumberOfItemsToBeProcessed(final int numberOfItemsToBeProcessed) {
		setIntValue(KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED, numberOfItemsToBeProcessed);
	}

	/**
	 * Returns the number of items to be processed.
	 * @return the number of items to be processed.
	 */
	public int getNumberOfItemsToBeProcessed() {
		return getIntValue(KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED);
	}

	/**
	 * Returns the number of failed items.
	 * @return the number of failed items.
	 */
	public int getNumberOfItemsFailed() {
		return getIntValue(KEY_NUMBER_OF_ITEMS_FAILED);
	}

	/**
	 * Increments the number of failed items.
	 */
	public void incrementFailedItems() {
		increment(KEY_NUMBER_OF_ITEMS_FAILED);
	}

	/**
	 * Reset items counters.
	 */
	public void resetCounters() {
		setIntValue(KEY_NUMBER_OF_ITEMS_PROCESSED, 0);
		setIntValue(KEY_NUMBER_OF_ITEMS_FAILED, 0);
		setIntValue(KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED, 0);
	}

	/**
	 * Returns the number of processed items.
	 * @return the number of processed items.
	 */
	public int getNumberOfItemsProcessed() {
		return getIntValue(KEY_NUMBER_OF_ITEMS_PROCESSED);
	}

	/**
	 * Returns the number of remaining items.
	 * @return the number of processed items.
	 */
	public int getNumberOfItemsRemaining() {
		return getNumberOfItemsToBeProcessed() - getNumberOfItemsProcessed() - getNumberOfItemsFailed();
	}

	/**
	 * Increments the number of processed items.
	 */
	public void incrementProcessedItems() {
		increment(KEY_NUMBER_OF_ITEMS_PROCESSED);
	}

	/**
	 * Set job {@link BatchJobStatus#RUNNING} status.
	 */
	public void setRunningStatus() {
		setStatusValue(BatchJobStatus.RUNNING);
	}

	/**
	 * Set job {@link BatchJobStatus#FINISHED} status.
	 */
	public void setFinishedStatus() {
		setStatusValue(BatchJobStatus.FINISHED);
	}

	/**
	 * Set job {@link BatchJobStatus#FAILED} status.
	 */
	public void setFailedStatus() {
		setStatusValue(BatchJobStatus.FAILED);
	}

	/**
	 * Returns the job {@link BatchJobStatus}.
	 * @return the job {@link BatchJobStatus}.
	 */
	public BatchJobStatus getStatus() {
		final var currentStatus = getStatusValue();
		if (BatchJobStatus.RUNNING.equals(currentStatus) && getNumberOfItemsFailed() == 0) {
			return BatchJobStatus.RUNNING;
		}
		else if (BatchJobStatus.RUNNING.equals(currentStatus) && getNumberOfItemsFailed() > 0) {
			return BatchJobStatus.RUNNING_WITH_FAILURES;
		}
		if (BatchJobStatus.FINISHED.equals(currentStatus) && getNumberOfItemsFailed() == 0) {
			return BatchJobStatus.FINISHED;
		}
		else if (BatchJobStatus.FINISHED.equals(currentStatus) && getNumberOfItemsFailed() > 0) {
			return BatchJobStatus.FINISHED_WITH_FAILURES;
		}
		else {
			return currentStatus;
		}
	}

	/**
	 * Returns the job {@link JobExecutionContext}.
	 * @return the job {@link JobExecutionContext}.
	 */
	public JobExecutionContext getJobExecutionContext() {
		return jobExecutionContext;
	}

	private void setIntValue(final String key, final int value) {
		jobExecutionContext.getJobDetail().getJobDataMap().put(key, Integer.valueOf(value));
	}

	private int getIntValue(final String key) {
		return getIntValue(key, 0);
	}

	private int getIntValue(final String key, final int defaultValue) {
		return Optional.ofNullable((Integer) jobExecutionContext.getJobDetail().getJobDataMap().get(key))
				.orElse(defaultValue);
	}

	private void increment(final String key) {
		final int value = getIntValue(key);
		setIntValue(key, value + 1);
	}

	private void setStatusValue(final BatchJobStatus value) {
		jobExecutionContext.getJobDetail().getJobDataMap().put(KEY_BATCH_JOB_STATUS, value);
	}

	private BatchJobStatus getStatusValue() {
		return (BatchJobStatus) Optional
				.ofNullable(jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_BATCH_JOB_STATUS))
				.orElse(BatchJobStatus.NOT_STARTED);
	}

}
