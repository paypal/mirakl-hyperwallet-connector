package com.paypal.kyc.statussynchronization.jobs;

import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.jobsystem.quartzadapter.support.AbstractBatchJobSupportQuartzJob;
import com.paypal.kyc.statussynchronization.batchjobs.KYCUserStatusResyncBatchJob;
import org.quartz.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class KYCUserStatusResyncJob extends AbstractBatchJobSupportQuartzJob implements Job {

	private final KYCUserStatusResyncBatchJob kycUserStatusResyncBatchJob;

	protected KYCUserStatusResyncJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
			final KYCUserStatusResyncBatchJob kycUserStatusResyncBatchJob) {
		super(quartzBatchJobAdapterFactory);
		this.kycUserStatusResyncBatchJob = kycUserStatusResyncBatchJob;
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		executeBatchJob(this.kycUserStatusResyncBatchJob, context);
	}

}
