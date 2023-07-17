package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.services.BatchJobExecutor;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.quartzadapter.jobcontext.QuartzBatchJobContextFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzBatchJobAdapter implements Job {

	private final BatchJobExecutor batchJobExecutor;

	private final BatchJob<BatchJobContext, BatchJobItem<?>> batchJob;

	private final QuartzBatchJobContextFactory quartzBatchJobContextFactory;

	public QuartzBatchJobAdapter(final BatchJobExecutor batchJobExecutor,
			final BatchJob<BatchJobContext, BatchJobItem<?>> batchJob,
			final QuartzBatchJobContextFactory quartzBatchJobContextFactory) {
		this.batchJobExecutor = batchJobExecutor;
		this.batchJob = batchJob;
		this.quartzBatchJobContextFactory = quartzBatchJobContextFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		final BatchJobContext batchJobContext = getBatchJobContext(context);

		batchJobExecutor.execute(batchJob, batchJobContext);
	}

	private BatchJobContext getBatchJobContext(final JobExecutionContext jobExecutionContext) {
		return quartzBatchJobContextFactory.getBatchJobContext(batchJob, jobExecutionContext);
	}

}
