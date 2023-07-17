package com.paypal.kyc.sellersdocumentextraction.batchjobs;

import com.paypal.kyc.documentextractioncommons.services.KYCReadyForReviewService;
import com.paypal.kyc.documentextractioncommons.support.AbstractDocumentsBatchJobItemProcessor;
import com.paypal.kyc.sellersdocumentextraction.services.HyperwalletSellerExtractService;
import com.paypal.kyc.sellersdocumentextraction.services.MiraklSellerDocumentsExtractService;
import org.springframework.stereotype.Component;

/**
 * Handles processing of seller documents by pushing them to Hyperwallet.
 */
@Component
public class SellersDocumentsExtractBatchJobItemProcessor
		extends AbstractDocumentsBatchJobItemProcessor<SellersDocumentsExtractBatchJobItem> {

	private final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService;

	private final HyperwalletSellerExtractService hyperwalletSellerExtractService;

	public SellersDocumentsExtractBatchJobItemProcessor(
			final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService,
			final HyperwalletSellerExtractService hyperwalletSellerExtractService,
			final KYCReadyForReviewService kycReadyForReviewService) {
		super(kycReadyForReviewService);
		this.miraklSellerDocumentsExtractService = miraklSellerDocumentsExtractService;
		this.hyperwalletSellerExtractService = hyperwalletSellerExtractService;
	}

	/**
	 * Push and flag the {@link SellersDocumentsExtractBatchJobItem} with the
	 * {@link HyperwalletSellerExtractService} and
	 * {@link MiraklSellerDocumentsExtractService}
	 * @param jobItem The {@link SellersDocumentsExtractBatchJobItem}
	 * @return If document was pushed to Hyperwallet
	 */
	@Override
	protected boolean pushAndFlagDocuments(final SellersDocumentsExtractBatchJobItem jobItem) {
		final boolean areDocumentsPushedToHW = hyperwalletSellerExtractService.pushDocuments(jobItem.getItem());

		if (areDocumentsPushedToHW) {
			miraklSellerDocumentsExtractService
					.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(jobItem.getItem());
		}

		return areDocumentsPushedToHW;
	}

}
