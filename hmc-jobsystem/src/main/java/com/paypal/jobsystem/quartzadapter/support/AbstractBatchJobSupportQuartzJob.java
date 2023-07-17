package com.paypal.jobsystem.quartzadapter.support;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class AbstractBatchJobSupportQuartzJob implements Job {

	private final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory;

	protected AbstractBatchJobSupportQuartzJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory) {
		this.quartzBatchJobAdapterFactory = quartzBatchJobAdapterFactory;
	}

	protected void executeBatchJob(final BatchJob<? extends BatchJobContext, ? extends BatchJobItem<?>> batchJob,
			final JobExecutionContext context) throws JobExecutionException {
		quartzBatchJobAdapterFactory.getQuartzJob(batchJob).execute(context);
	}

}
