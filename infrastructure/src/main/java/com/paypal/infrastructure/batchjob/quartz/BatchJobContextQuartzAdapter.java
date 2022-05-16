package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobStatus;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import java.util.Optional;
import java.util.UUID;

public class BatchJobContextQuartzAdapter implements BatchJobContext {

	private static final String KEY_BATCH_JOB_EXECUTION_UUID = "batchJobUuid";

	private static final String KEY_BATCH_JOB_STATUS = "batchJobStatus";

	private static final String KEY_NUMBER_OF_ITEMS_PROCESSED = "numberOfItemsProcessed";

	private static final String KEY_NUMBER_OF_ITEMS_FAILED = "numberOfItemsFailed";

	private static final String KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED = "numberOfItemsToBeProcessed";

	private final JobExecutionContext jobExecutionContext;

	public BatchJobContextQuartzAdapter(final JobExecutionContext jobExecutionContext) {
		this.jobExecutionContext = jobExecutionContext;
		if (StringUtils.isEmpty(getStringValue(KEY_BATCH_JOB_EXECUTION_UUID))) {
			setStringValue(KEY_BATCH_JOB_EXECUTION_UUID, UUID.randomUUID().toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getJobName() {
		return jobExecutionContext.getJobDetail().getKey().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getJobUuid() {
		return getStringValue(KEY_BATCH_JOB_EXECUTION_UUID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNumberOfItemsToBeProcessed(final int numberOfItemsToBeProcessed) {
		setIntValue(KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED, numberOfItemsToBeProcessed);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfItemsToBeProcessed() {
		return getIntValue(KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfItemsFailed() {
		return getIntValue(KEY_NUMBER_OF_ITEMS_FAILED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incrementFailedItems() {
		increment(KEY_NUMBER_OF_ITEMS_FAILED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetCounters() {
		setIntValue(KEY_NUMBER_OF_ITEMS_PROCESSED, 0);
		setIntValue(KEY_NUMBER_OF_ITEMS_FAILED, 0);
		setIntValue(KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfItemsProcessed() {
		return getIntValue(KEY_NUMBER_OF_ITEMS_PROCESSED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfItemsRemaining() {
		return getNumberOfItemsToBeProcessed() - getNumberOfItemsProcessed() - getNumberOfItemsFailed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incrementProcessedItems() {
		increment(KEY_NUMBER_OF_ITEMS_PROCESSED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRunningStatus() {
		setStatusValue(BatchJobStatus.RUNNING);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFinishedStatus() {
		setStatusValue(BatchJobStatus.FINISHED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFailedStatus() {
		setStatusValue(BatchJobStatus.FAILED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	 * {@inheritDoc}
	 */
	@Override
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

	private String getStringValue(final String key) {
		return (String) jobExecutionContext.getJobDetail().getJobDataMap().get(key);
	}

	private void setStringValue(final String key, String value) {
		jobExecutionContext.getJobDetail().getJobDataMap().put(key, value);
	}

}
