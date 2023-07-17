package com.paypal.jobsystem.quartzadapter.jobcontext;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobStatus;
import org.quartz.JobExecutionContext;

import java.util.Optional;
import java.util.UUID;

public class QuartzBatchJobContextAdapter implements BatchJobContext {

	protected static final String KEY_BATCH_JOB = "batchJob";

	protected static final String KEY_BATCH_JOB_NAME = "batchJobName";

	protected static final String KEY_BATCH_JOB_EXECUTION_UUID = "batchJobUuid";

	protected static final String KEY_BATCH_JOB_STATUS = "batchJobStatus";

	protected static final String KEY_NUMBER_OF_ITEMS_PROCESSED = "numberOfItemsProcessed";

	protected static final String KEY_NUMBER_OF_ITEMS_FAILED = "numberOfItemsFailed";

	protected static final String KEY_NUMBER_OF_ITEMS_TO_BE_PROCESSED = "numberOfItemsToBeProcessed";

	protected static final String KEY_PARTIAL_ITEM_EXTRACTION = "partialItemExtraction";

	public static final String KEY_NUMBER_OF_ITEMS_NOT_SUCCESFULLY_EXTRACTED = "numberOfItemsNotSuccesfullyExtracted";

	private final JobExecutionContext jobExecutionContext;

	public QuartzBatchJobContextAdapter(final JobExecutionContext jobExecutionContext) {
		this.jobExecutionContext = jobExecutionContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getJobName() {
		return getStringValue(KEY_BATCH_JOB_NAME);
	}

	protected void setJobName(final String jobName) {
		setStringValue(KEY_BATCH_JOB_NAME, jobName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getJobUuid() {
		return getStringValue(KEY_BATCH_JOB_EXECUTION_UUID);
	}

	@Override
	public boolean isPartialItemExtraction() {
		return getBooleanValue(KEY_PARTIAL_ITEM_EXTRACTION);
	}

	public void setPartialItemExtraction(final boolean partialItemExtraction) {
		setBooleanValue(KEY_PARTIAL_ITEM_EXTRACTION, partialItemExtraction);
	}

	@Override
	public Optional<Integer> getNumberOfItemsNotSuccessfullyExtracted() {
		return getOptionalIntValue(KEY_NUMBER_OF_ITEMS_NOT_SUCCESFULLY_EXTRACTED);
	}

	@Override
	public void setNumberOfItemsNotSuccessfullyExtracted(final int numberOfItemsNotSuccessfullyExtracted) {
		setIntValue(KEY_NUMBER_OF_ITEMS_NOT_SUCCESFULLY_EXTRACTED, numberOfItemsNotSuccessfullyExtracted);
	}

	protected void initializeBatchJobUuid() {
		setStringValue(KEY_BATCH_JOB_EXECUTION_UUID, UUID.randomUUID().toString());
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

	@Override
	public void setFinishedWithFailuresStatus() {
		setStatusValue(BatchJobStatus.FINISHED_WITH_FAILURES);
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

	@Override
	public <C extends BatchJobContext, T extends BatchJobItem<?>> BatchJob<C, T> getBatchJob() {
		return (BatchJob) jobExecutionContext.getJobDetail().getJobDataMap().get(KEY_BATCH_JOB);
	}

	protected void setBatchJob(final BatchJob<?, ?> batchJob) {
		jobExecutionContext.getJobDetail().getJobDataMap().put(QuartzBatchJobContextAdapter.KEY_BATCH_JOB, batchJob);
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

	private Optional<Integer> getOptionalIntValue(final String key) {
		return Optional.ofNullable((Integer) jobExecutionContext.getJobDetail().getJobDataMap().get(key));
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

	private void setStringValue(final String key, final String value) {
		jobExecutionContext.getJobDetail().getJobDataMap().put(key, value);
	}

	private boolean getBooleanValue(final String key) {
		return jobExecutionContext.getJobDetail().getJobDataMap().get(key) != null
				&& (Boolean) jobExecutionContext.getJobDetail().getJobDataMap().get(key);
	}

	private void setBooleanValue(final String key, final boolean value) {
		jobExecutionContext.getJobDetail().getJobDataMap().put(key, value);
	}

}
