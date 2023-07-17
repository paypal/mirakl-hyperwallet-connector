package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.services.BatchJobExecutor;
import com.paypal.jobsystem.quartzadapter.jobcontext.QuartzBatchJobContextFactory;
import org.quartz.Job;
import org.springframework.stereotype.Component;

@Component
public class QuartzBatchJobAdapterFactoryImpl implements QuartzBatchJobAdapterFactory {

	private final BatchJobExecutor batchJobExecutor;

	private final QuartzBatchJobContextFactory quartzBatchJobContextFactory;

	public QuartzBatchJobAdapterFactoryImpl(final BatchJobExecutor batchJobExecutor,
			final QuartzBatchJobContextFactory quartzBatchJobContextFactory) {
		this.batchJobExecutor = batchJobExecutor;
		this.quartzBatchJobContextFactory = quartzBatchJobContextFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Job getQuartzJob(final BatchJob batchJob) {
		return new QuartzBatchJobAdapter(batchJobExecutor, batchJob, quartzBatchJobContextFactory);
	}

}
