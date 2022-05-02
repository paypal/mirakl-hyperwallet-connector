package com.paypal.kyc.batchjobs.sellers;

import com.paypal.kyc.batchjobs.AbstractDocumentsBatchJobItemProcessor;
import com.paypal.kyc.service.KYCReadyForReviewService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletSellerExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import org.springframework.stereotype.Service;

/**
 * Handles processing of seller documents by pushing them to Hyperwallet.
 */
@Service
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
		boolean areDocumentsPushedToHW = hyperwalletSellerExtractService.pushDocuments(jobItem.getItem());

		if (areDocumentsPushedToHW) {
			miraklSellerDocumentsExtractService
					.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(jobItem.getItem());
		}

		return areDocumentsPushedToHW;
	}

}
