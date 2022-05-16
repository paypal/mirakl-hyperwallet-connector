package com.paypal.sellers.jobs;

import com.paypal.infrastructure.batchjob.quartz.AbstractBatchJobSupportQuartzJob;
import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.batchjobs.individuals.IndividualSellersExtractBatchJob;
import org.quartz.*;

/**
 * Quartz Job for executing the {@link IndividualSellersExtractBatchJob}.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class IndividualSellersExtractJob extends AbstractBatchJobSupportQuartzJob implements Job {

	private final IndividualSellersExtractBatchJob individualSellersExtractBatchJob;

	public IndividualSellersExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
			final IndividualSellersExtractBatchJob individualSellersExtractBatchJob) {
		super(quartzBatchJobAdapterFactory);
		this.individualSellersExtractBatchJob = individualSellersExtractBatchJob;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		executeBatchJob(individualSellersExtractBatchJob, context);
	}

}
