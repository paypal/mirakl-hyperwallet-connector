package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobExecutor;
import org.quartz.Job;
import org.springframework.stereotype.Service;

@Service
public class QuartzBatchJobAdapterFactoryImpl implements QuartzBatchJobAdapterFactory {

	private final BatchJobExecutor batchJobExecutor;

	private final QuartzBatchJobContextFactory quartzBatchJobContextFactory;

	public QuartzBatchJobAdapterFactoryImpl(BatchJobExecutor batchJobExecutor,
			QuartzBatchJobContextFactory quartzBatchJobContextFactory) {
		this.batchJobExecutor = batchJobExecutor;
		this.quartzBatchJobContextFactory = quartzBatchJobContextFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Job getQuartzJob(BatchJob batchJob) {
		return new QuartzBatchJobAdapter(batchJobExecutor, batchJob, quartzBatchJobContextFactory);
	}

}
