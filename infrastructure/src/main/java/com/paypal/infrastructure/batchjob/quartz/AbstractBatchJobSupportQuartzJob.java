package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class AbstractBatchJobSupportQuartzJob implements Job {

	private final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory;

	protected AbstractBatchJobSupportQuartzJob(QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory) {
		this.quartzBatchJobAdapterFactory = quartzBatchJobAdapterFactory;
	}

	protected void executeBatchJob(BatchJob<? extends BatchJobContext, ? extends BatchJobItem<?>> batchJob,
			JobExecutionContext context) throws JobExecutionException {
		quartzBatchJobAdapterFactory.getQuartzJob(batchJob).execute(context);
	}

}
