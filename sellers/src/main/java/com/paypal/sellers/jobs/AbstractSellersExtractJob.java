package com.paypal.sellers.jobs;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.sellers.sellersextract.service.SellersExtractService;

import javax.annotation.Resource;

/**
 * Abstract class that holds
 * {@link com.paypal.infrastructure.repository.JobExecutionInformationRepository} and
 * {@link SellersExtractService} dependencies and helps with methods for extract sellers
 * job
 */
public abstract class AbstractSellersExtractJob extends AbstractDeltaInfoJob {

	@Resource
	protected SellersExtractService sellersExtractService;

}
