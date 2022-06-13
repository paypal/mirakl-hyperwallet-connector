package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

@Service
public class QuartzBatchJobContextFactoryImpl implements QuartzBatchJobContextFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BatchJobContext getBatchJobContext(BatchJob<BatchJobContext, BatchJobItem<?>> batchJob,
			JobExecutionContext jobExecutionContext) {
		jobExecutionContext.getJobDetail().getJobDataMap().put(BatchJobContextQuartzAdapter.KEY_BATCH_JOB, batchJob);
		return new BatchJobContextQuartzAdapter(jobExecutionContext);
	}

}
