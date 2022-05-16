package com.paypal.infrastructure.batchjob;

import org.quartz.JobExecutionContext;

/**
 * Holds batch job context info.
 */
public interface BatchJobContext {

	/**
	 * Returns the job class name.
	 * @return the job name.
	 */
	String getJobName();

	String getJobUuid();

	/**
	 * Sets the number of items to be processed.
	 * @param numberOfItemsToBeProcessed the number of items to be processed.
	 */
	void setNumberOfItemsToBeProcessed(int numberOfItemsToBeProcessed);

	/**
	 * Returns the number of items to be processed.
	 * @return the number of items to be processed.
	 */
	int getNumberOfItemsToBeProcessed();

	/**
	 * Returns the number of failed items.
	 * @return the number of failed items.
	 */
	int getNumberOfItemsFailed();

	/**
	 * Increments the number of failed items.
	 */
	void incrementFailedItems();

	/**
	 * Reset items counters.
	 */
	void resetCounters();

	/**
	 * Returns the number of processed items.
	 * @return the number of processed items.
	 */
	int getNumberOfItemsProcessed();

	/**
	 * Returns the number of remaining items.
	 * @return the number of processed items.
	 */
	int getNumberOfItemsRemaining();

	/**
	 * Increments the number of processed items.
	 */
	void incrementProcessedItems();

	/**
	 * Set job {@link BatchJobStatus#RUNNING} status.
	 */
	void setRunningStatus();

	/**
	 * Set job {@link BatchJobStatus#FINISHED} status.
	 */
	void setFinishedStatus();

	/**
	 * Set job {@link BatchJobStatus#FAILED} status.
	 */
	void setFailedStatus();

	/**
	 * Returns the job {@link BatchJobStatus}.
	 * @return the job {@link BatchJobStatus}.
	 */
	BatchJobStatus getStatus();

	/**
	 * Returns the job {@link JobExecutionContext}.
	 * @return the job {@link JobExecutionContext}.
	 */
	JobExecutionContext getJobExecutionContext();

}
