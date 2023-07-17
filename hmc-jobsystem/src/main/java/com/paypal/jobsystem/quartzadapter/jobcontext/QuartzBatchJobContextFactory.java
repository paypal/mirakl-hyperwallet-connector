package com.paypal.jobsystem.quartzadapter.jobcontext;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import org.quartz.JobExecutionContext;

public interface QuartzBatchJobContextFactory {

	/**
	 * Creates a {@link BatchJobContext} by the given {@link JobExecutionContext}.
	 * @param batchJob a {@link BatchJob}.
	 * @param jobExecutionContext a {@link JobExecutionContext}.
	 * @return a {@link BatchJobContext}.
	 */
	BatchJobContext getBatchJobContext(BatchJob<BatchJobContext, BatchJobItem<?>> batchJob,
			JobExecutionContext jobExecutionContext);

}
