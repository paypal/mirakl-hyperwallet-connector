package com.paypal.kyc.documentextractioncommons.jobs;

import com.paypal.jobsystem.quartzadapter.support.AbstractBatchJobSupportQuartzJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJob;
import com.paypal.kyc.sellersdocumentextraction.batchjobs.SellersDocumentsExtractBatchJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * Extract documents job for extracting Mirakl sellers documents data and populate them on
 * HyperWallet for KYC validation
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DocumentsExtractJob extends AbstractBatchJobSupportQuartzJob implements Job {

	private final SellersDocumentsExtractBatchJob sellersDocumentsExtractBatchJob;

	private final BusinessStakeholdersDocumentsExtractBatchJob businessStakeholdersDocumentsExtractBatchJob;

	public DocumentsExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
			final SellersDocumentsExtractBatchJob sellersDocumentsExtractBatchJob,
			final BusinessStakeholdersDocumentsExtractBatchJob businessStakeholdersDocumentsExtractBatchJob) {
		super(quartzBatchJobAdapterFactory);
		this.sellersDocumentsExtractBatchJob = sellersDocumentsExtractBatchJob;
		this.businessStakeholdersDocumentsExtractBatchJob = businessStakeholdersDocumentsExtractBatchJob;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		executeBatchJob(sellersDocumentsExtractBatchJob, context);
		executeBatchJob(businessStakeholdersDocumentsExtractBatchJob, context);
	}

}
