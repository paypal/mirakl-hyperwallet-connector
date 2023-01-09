package com.paypal.infrastructure.batchjob;

import org.quartz.JobExecutionContext;

import java.util.Optional;

/**
 * Holds batch job context info.
 */
public interface BatchJobContext {

	/**
	 * Returns the job class name.
	 * @return the job name.
	 */
	String getJobName();

	/**
	 * Returns the job unique identifier.
	 * @return the job unique identifier.
	 */
	String getJobUuid();

	/**
	 * Returns if the item extraction has not been fully successful and only a part of all
	 * the potential items to be processed were retrieved.
	 * @return whether the item extraction has been partial or fully successful.
	 */
	boolean isPartialItemExtraction();

	/**
	 * Sets if the extraction has not been fully successful and only a part of all the
	 * potential items to be processed were retrieved.
	 * @param partialItemExtraction whether the item extraction has been partial or fully
	 * successful.
	 */
	void setPartialItemExtraction(boolean partialItemExtraction);

	/**
	 * Returns the number of items that couldn't be extracted, if this number is known.
	 * @return the number of items that couldn't be extracted.
	 */
	Optional<Integer> getNumberOfItemsNotSuccessfullyExtracted();

	/**
	 * Sets the number of items that couldn't be extracted.
	 * @param numberOfItemsNotSuccessfullyExtracted the number of items that couldn't be
	 * extracted.
	 */
	void setNumberOfItemsNotSuccessfullyExtracted(int numberOfItemsNotSuccessfullyExtracted);

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
	 * Set job {@link BatchJobStatus#FINISHED_WITH_FAILURES} status.
	 */
	void setFinishedWithFailuresStatus();

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

	/**
	 * Returns the job which is being executed.
	 * @return the {@link BatchJob}.
	 */
	<C extends BatchJobContext, T extends BatchJobItem<?>> BatchJob<C, T> getBatchJob();

}
