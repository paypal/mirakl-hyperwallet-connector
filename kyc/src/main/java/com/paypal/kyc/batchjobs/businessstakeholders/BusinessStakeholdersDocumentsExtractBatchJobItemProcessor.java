package com.paypal.kyc.batchjobs.businessstakeholders;

import com.paypal.kyc.batchjobs.AbstractDocumentsBatchJobItemProcessor;
import com.paypal.kyc.service.KYCReadyForReviewService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import org.springframework.stereotype.Service;

/**
 * Handles processing of business stakeholder documents by pushing them to Hyperwallet.
 */
@Service
public class BusinessStakeholdersDocumentsExtractBatchJobItemProcessor
		extends AbstractDocumentsBatchJobItemProcessor<BusinessStakeholdersDocumentsExtractBatchJobItem> {

	private final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService;

	private final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService;

	public BusinessStakeholdersDocumentsExtractBatchJobItemProcessor(
			MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService,
			HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService,
			KYCReadyForReviewService kycReadyForReviewService) {
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
		boolean areDocumentsPushedToHW = hyperwalletBusinessStakeholderExtractService.pushDocuments(jobItem.getItem());

		if (areDocumentsPushedToHW) {
			miraklBusinessStakeholderDocumentsExtractService
					.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(jobItem.getItem());
		}

		return areDocumentsPushedToHW;
	}

}
