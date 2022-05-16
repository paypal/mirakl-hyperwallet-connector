package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

@Service
public class QuartzBatchJobContextFactoryImpl implements QuartzBatchJobContextFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BatchJobContext getBatchJobContext(JobExecutionContext jobExecutionContext) {
		return new BatchJobContextQuartzAdapter(jobExecutionContext);
	}

}
