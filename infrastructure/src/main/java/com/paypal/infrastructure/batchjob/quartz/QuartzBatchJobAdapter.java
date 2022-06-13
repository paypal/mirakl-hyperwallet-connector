package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobExecutor;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzBatchJobAdapter implements Job {

	private final BatchJobExecutor batchJobExecutor;

	private final BatchJob<BatchJobContext, BatchJobItem<?>> batchJob;

	private final QuartzBatchJobContextFactory quartzBatchJobContextFactory;

	public QuartzBatchJobAdapter(BatchJobExecutor batchJobExecutor, BatchJob<BatchJobContext, BatchJobItem<?>> batchJob,
			QuartzBatchJobContextFactory quartzBatchJobContextFactory) {
		this.batchJobExecutor = batchJobExecutor;
		this.batchJob = batchJob;
		this.quartzBatchJobContextFactory = quartzBatchJobContextFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		BatchJobContext batchJobContext = getBatchJobContext(context);

		batchJobExecutor.execute(batchJob, batchJobContext);
	}

	private BatchJobContext getBatchJobContext(final JobExecutionContext jobExecutionContext) {
		return quartzBatchJobContextFactory.getBatchJobContext(batchJob, jobExecutionContext);
	}

}
