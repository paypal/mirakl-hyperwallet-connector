package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import org.quartz.JobExecutionContext;

public interface QuartzBatchJobContextFactory {

	/**
	 * Creates a {@link BatchJobContext} by the given {@link JobExecutionContext}.
	 * @param jobExecutionContext a {@link JobExecutionContext}.
	 * @return a {@link BatchJobContext}.
	 */
	BatchJobContext getBatchJobContext(JobExecutionContext jobExecutionContext);

}
