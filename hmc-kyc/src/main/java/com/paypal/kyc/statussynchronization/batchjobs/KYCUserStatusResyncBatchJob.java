package com.paypal.kyc.statussynchronization.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractExtractBatchJob;
import org.springframework.stereotype.Component;

/**
 * It takes the info in a Hyperwallet user and processes it as a status notification.
 */
@Component
public class KYCUserStatusResyncBatchJob
		extends AbstractExtractBatchJob<BatchJobContext, KYCUserStatusResyncBatchJobItem> {

	private final KYCUserStatusResyncBatchJobItemProcessor kycUserStatusResyncBatchJobItemProcessor;

	private final KYCUserStatusResyncBatchJobItemExtractor kycUserStatusResyncBatchJobItemExtractor;

	public KYCUserStatusResyncBatchJob(
			final KYCUserStatusResyncBatchJobItemProcessor kycUserStatusResyncBatchJobItemProcessor,
			final KYCUserStatusResyncBatchJobItemExtractor kycUserStatusResyncBatchJobItemExtractor) {
		this.kycUserStatusResyncBatchJobItemProcessor = kycUserStatusResyncBatchJobItemProcessor;
		this.kycUserStatusResyncBatchJobItemExtractor = kycUserStatusResyncBatchJobItemExtractor;
	}

	@Override
	protected BatchJobItemProcessor<BatchJobContext, KYCUserStatusResyncBatchJobItem> getBatchJobItemProcessor() {
		return this.kycUserStatusResyncBatchJobItemProcessor;
	}

	@Override
	protected BatchJobItemsExtractor<BatchJobContext, KYCUserStatusResyncBatchJobItem> getBatchJobItemsExtractor() {
		return this.kycUserStatusResyncBatchJobItemExtractor;
	}

}
