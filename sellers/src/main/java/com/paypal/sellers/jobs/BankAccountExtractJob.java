package com.paypal.sellers.jobs;

import com.paypal.infrastructure.batchjob.quartz.AbstractBatchJobSupportQuartzJob;
import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobAdapterFactory;
import com.paypal.sellers.batchjobs.bankaccount.BankAccountExtractBatchJob;
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
