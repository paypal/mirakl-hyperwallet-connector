package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class QuartzBatchJobBean extends QuartzJobBean {

	public static final String KEY_BATCH_JOB_BEAN = "batchJob";

	@Autowired
	private QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory;

	private BatchJob<? extends BatchJobContext, ? extends BatchJobItem<?>> batchJob;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeInternal(final JobExecutionContext context) throws JobExecutionException {
		quartzBatchJobAdapterFactory.getQuartzJob(batchJob).execute(context);
	}

	public void setBatchJob(final BatchJob<BatchJobContext, BatchJobItem<?>> batchJob) {
		this.batchJob = batchJob;
	}

	public static Class<?> getBatchJobClass(final JobExecutionContext context) {
		return context.getJobDetail().getJobDataMap().get(KEY_BATCH_JOB_BEAN).getClass();
	}

}
