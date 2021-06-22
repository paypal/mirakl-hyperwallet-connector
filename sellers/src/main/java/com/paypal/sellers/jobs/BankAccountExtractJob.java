package com.paypal.sellers.jobs;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.sellers.bankaccountextract.service.BankAccountExtractService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;

import javax.annotation.Resource;

/**
 * Extract sellers job for extracting Mirakl sellers data and populate it on HyperWallet
 * as users
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class BankAccountExtractJob extends AbstractDeltaInfoJob {

	@Resource
	private BankAccountExtractService bankAccountExtractService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) {
		bankAccountExtractService.extractBankAccounts(getDelta(context));
	}

}
