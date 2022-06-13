package com.paypal.infrastructure.batchjob;

/**
 * Abstract class for all jobs of type Extract
 */
public abstract class AbstractExtractBatchJob<C extends BatchJobContext, T extends BatchJobItem<?>>
		extends AbstractBatchJob<C, T> {

	@Override
	public BatchJobType getType() {
		return BatchJobType.EXTRACT;
	}

}
