package com.paypal.sellers.jobs;

import com.paypal.sellers.batchjobs.individuals.IndividualSellersExtractBatchJob;
import org.quartz.*;

/**
 * Quartz Job for executing the {@link IndividualSellersExtractBatchJob}.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class IndividualSellersExtractJob implements Job {

	private final IndividualSellersExtractBatchJob individualSellersExtractBatchJob;

	public IndividualSellersExtractJob(final IndividualSellersExtractBatchJob individualSellersExtractBatchJob) {
		this.individualSellersExtractBatchJob = individualSellersExtractBatchJob;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		individualSellersExtractBatchJob.execute(context);
	}

}
