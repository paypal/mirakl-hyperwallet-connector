package com.paypal.sellers.professionalsellersextraction.jobs;

import com.paypal.jobsystem.quartzadapter.support.AbstractBatchJobSupportQuartzJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.stakeholdersextraction.batchjobs.BusinessStakeholdersExtractBatchJob;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersExtractBatchJob;
import org.quartz.*;

/**
 * Quartz Job for executing the {@link ProfessionalSellersExtractBatchJob}.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ProfessionalSellersExtractJob extends AbstractBatchJobSupportQuartzJob implements Job {

	private final ProfessionalSellersExtractBatchJob professionalSellersExtractBatchJob;

	private final BusinessStakeholdersExtractBatchJob businessStakeholdersExtractBatchJob;

	public ProfessionalSellersExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
			final ProfessionalSellersExtractBatchJob professionalSellersExtractBatchJob,
			final BusinessStakeholdersExtractBatchJob businessStakeholdersExtractBatchJob) {
		super(quartzBatchJobAdapterFactory);
		this.professionalSellersExtractBatchJob = professionalSellersExtractBatchJob;
		this.businessStakeholdersExtractBatchJob = businessStakeholdersExtractBatchJob;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		executeBatchJob(professionalSellersExtractBatchJob, context);
		executeBatchJob(businessStakeholdersExtractBatchJob, context);
	}

}
