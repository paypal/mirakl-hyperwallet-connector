package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import org.quartz.Job;

public interface QuartzBatchJobAdapterFactory {

	/**
	 * Create a {@link Job} by the given {@link BatchJob}.
	 * @param batchJob a {@link BatchJob}.
	 * @return a {@link Job}.
	 */
	Job getQuartzJob(BatchJob<? extends BatchJobContext, ? extends BatchJobItem<?>> batchJob);

}
