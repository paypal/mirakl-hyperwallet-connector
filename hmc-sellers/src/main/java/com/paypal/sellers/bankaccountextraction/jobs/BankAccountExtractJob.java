package com.paypal.sellers.bankaccountextraction.jobs;

import com.paypal.jobsystem.quartzadapter.support.AbstractBatchJobSupportQuartzJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.bankaccountextraction.batchjobs.BankAccountExtractBatchJob;
import org.quartz.*;

/**
 * Quartz Job for executing the {@link BankAccountExtractBatchJob}.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class BankAccountExtractJob extends AbstractBatchJobSupportQuartzJob implements Job {

	private final BankAccountExtractBatchJob bankAccountExtractBatchJob;

	public BankAccountExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
			final BankAccountExtractBatchJob bankAccountExtractBatchJob) {
		super(quartzBatchJobAdapterFactory);
		this.bankAccountExtractBatchJob = bankAccountExtractBatchJob;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		executeBatchJob(bankAccountExtractBatchJob, context);
	}

}
