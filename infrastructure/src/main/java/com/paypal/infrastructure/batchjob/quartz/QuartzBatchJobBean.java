package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
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
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		quartzBatchJobAdapterFactory.getQuartzJob(batchJob).execute(context);
	}

	public void setBatchJob(BatchJob<? extends BatchJobContext, ? extends BatchJobItem<?>> batchJob) {
		this.batchJob = batchJob;
	}

	public static Class<?> getBatchJobClass(JobExecutionContext context) {
		return context.getJobDetail().getJobDataMap().get(KEY_BATCH_JOB_BEAN).getClass();
	}

}
