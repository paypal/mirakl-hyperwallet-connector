package com.paypal.kyc.stakeholdersdocumentextraction.batchjobs;

import com.paypal.kyc.documentextractioncommons.services.KYCReadyForReviewService;
import com.paypal.kyc.documentextractioncommons.support.AbstractDocumentsBatchJobItemProcessor;
import com.paypal.kyc.stakeholdersdocumentextraction.services.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.stakeholdersdocumentextraction.services.MiraklBusinessStakeholderDocumentsExtractService;
import org.springframework.stereotype.Component;

/**
 * Handles processing of business stakeholder documents by pushing them to Hyperwallet.
 */
@Component
public class BusinessStakeholdersDocumentsExtractBatchJobItemProcessor
		extends AbstractDocumentsBatchJobItemProcessor<BusinessStakeholdersDocumentsExtractBatchJobItem> {

	private final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService;

	private final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService;

	public BusinessStakeholdersDocumentsExtractBatchJobItemProcessor(
			final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService,
			final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService,
			final KYCReadyForReviewService kycReadyForReviewService) {
		super(kycReadyForReviewService);
		this.miraklBusinessStakeholderDocumentsExtractService = miraklBusinessStakeholderDocumentsExtractService;
		this.hyperwalletBusinessStakeholderExtractService = hyperwalletBusinessStakeholderExtractService;
	}

	/**
	 * Push and flag the {@link BusinessStakeholdersDocumentsExtractBatchJobItem} with the
	 * {@link HyperwalletBusinessStakeholderExtractService} and
	 * {@link MiraklBusinessStakeholderDocumentsExtractService}
	 * @param jobItem The {@link BusinessStakeholdersDocumentsExtractBatchJobItem}
	 * @return If document was pushed to Hyperwallet
	 */
	@Override
	protected boolean pushAndFlagDocuments(final BusinessStakeholdersDocumentsExtractBatchJobItem jobItem) {
		final boolean areDocumentsPushedToHW = hyperwalletBusinessStakeholderExtractService
				.pushDocuments(jobItem.getItem());

		if (areDocumentsPushedToHW) {
			miraklBusinessStakeholderDocumentsExtractService
					.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(jobItem.getItem());
		}

		return areDocumentsPushedToHW;
	}

}
