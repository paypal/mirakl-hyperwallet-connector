package com.paypal.sellers.jobs;

import com.paypal.sellers.batchjobs.bankaccount.BankAccountExtractBatchJob;
import org.quartz.*;

/**
 * Quartz Job for executing the {@link BankAccountExtractBatchJob}.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class BankAccountExtractJob implements Job {

	private final BankAccountExtractBatchJob bankAccountExtractBatchJob;

	public BankAccountExtractJob(final BankAccountExtractBatchJob bankAccountExtractBatchJob) {
		this.bankAccountExtractBatchJob = bankAccountExtractBatchJob;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		bankAccountExtractBatchJob.execute(context);
	}

}
