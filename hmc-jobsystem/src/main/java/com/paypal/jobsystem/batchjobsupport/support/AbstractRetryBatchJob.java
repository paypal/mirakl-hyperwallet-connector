package com.paypal.jobsystem.batchjobsupport.support;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobType;

/**
 * Abstract class for all jobs of type Retry
 */
public abstract class AbstractRetryBatchJob<C extends BatchJobContext, T extends BatchJobItem<?>>
		extends AbstractBatchJob<C, T> {

	@Override
	public BatchJobType getType() {
		return BatchJobType.RETRY;
	}

}
