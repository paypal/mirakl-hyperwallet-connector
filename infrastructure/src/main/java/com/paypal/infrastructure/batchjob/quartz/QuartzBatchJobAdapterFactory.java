package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.quartz.Job;

public interface QuartzBatchJobAdapterFactory {

	/**
	 * Create a {@link Job} by the given {@link BatchJob}.
	 * @param batchJob a {@link BatchJob}.
	 * @return a {@link Job}.
	 */
	Job getQuartzJob(BatchJob<? extends BatchJobContext, ? extends BatchJobItem<?>> batchJob);

}
