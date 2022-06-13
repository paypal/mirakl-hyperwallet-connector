package com.paypal.infrastructure.batchjob;

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
