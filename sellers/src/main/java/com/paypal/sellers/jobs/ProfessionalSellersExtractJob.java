package com.paypal.sellers.jobs;

import com.paypal.sellers.batchjobs.bstk.BusinessStakeholdersExtractBatchJob;
import com.paypal.sellers.batchjobs.professionals.ProfessionalSellersExtractBatchJob;
import org.quartz.*;

/**
 * Quartz Job for executing the {@link ProfessionalSellersExtractBatchJob}.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ProfessionalSellersExtractJob implements Job {

	private final ProfessionalSellersExtractBatchJob professionalSellersExtractBatchJob;

	private final BusinessStakeholdersExtractBatchJob businessStakeholdersExtractBatchJob;

	public ProfessionalSellersExtractJob(final ProfessionalSellersExtractBatchJob professionalSellersExtractBatchJob,
			final BusinessStakeholdersExtractBatchJob businessStakeholdersExtractBatchJob) {
		this.professionalSellersExtractBatchJob = professionalSellersExtractBatchJob;
		this.businessStakeholdersExtractBatchJob = businessStakeholdersExtractBatchJob;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		professionalSellersExtractBatchJob.execute(context);
		businessStakeholdersExtractBatchJob.execute(context);
	}

}
