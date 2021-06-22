package com.paypal.kyc.jobs;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.kyc.service.DocumentsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;

import javax.annotation.Resource;

/**
 * Extract documents job for extracting Mirakl sellers documents data and populate them on
 * HyperWallet for KYC validation
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DocumentsExtractJob extends AbstractDeltaInfoJob {

	@Resource
	private DocumentsExtractService documentsExtractService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) {
		documentsExtractService.extractProofOfIdentityAndBusinessSellerDocuments(getDelta(context));
		documentsExtractService.extractBusinessStakeholderDocuments(getDelta(context));
	}

}
