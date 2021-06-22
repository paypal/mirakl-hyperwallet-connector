package com.paypal.sellers.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;

/**
 * Extract sellers job for extracting Mirakl sellers data and populate it on HyperWallet
 * as users
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ProfessionalSellersExtractJob extends AbstractSellersExtractJob {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) {
		sellersExtractService.extractProfessionals(getDelta(context));
	}

}
