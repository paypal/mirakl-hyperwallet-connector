package com.paypal.jobsystem.batchjobsupport.support;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;

import java.util.Collection;
import java.util.Date;

public abstract class AbstractBatchJobItemExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		implements BatchJobItemsExtractor<C, T> {

	private static final String DELTA = "delta";

	protected AbstractBatchJobItemExtractor() {

	}

	protected Date getDelta(final C context) {
		final Date deltaParameter = findDeltaInJobParameters(context);

		return deltaParameter != null ? deltaParameter : getCalculatedDelta(context);
	}

	private Date findDeltaInJobParameters(final C context) {
		return (Date) context.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<T> getItems(final C ctx) {
		return getItems(ctx, getDelta(ctx));
	}

	protected abstract Collection<T> getItems(C ctx, Date delta);

	protected abstract Date getCalculatedDelta(final C context);

}
