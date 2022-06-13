package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
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
