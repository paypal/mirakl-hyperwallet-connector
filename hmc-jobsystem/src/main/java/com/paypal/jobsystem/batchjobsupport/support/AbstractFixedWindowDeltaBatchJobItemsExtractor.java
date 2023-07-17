package com.paypal.jobsystem.batchjobsupport.support;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public abstract class AbstractFixedWindowDeltaBatchJobItemsExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		extends AbstractBatchJobItemExtractor<C, T> {

	@Value("${hmc.jobs.settings.resync-maxdays}")
	protected Long resyncMaxDays;

	@Override
	protected Date getCalculatedDelta(final C context) {
		return new Date(System.currentTimeMillis() - resyncMaxDays * 24 * 60 * 60 * 1000);
	}

}
