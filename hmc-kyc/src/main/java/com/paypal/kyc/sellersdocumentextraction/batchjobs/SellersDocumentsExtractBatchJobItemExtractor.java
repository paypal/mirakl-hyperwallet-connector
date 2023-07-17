package com.paypal.kyc.sellersdocumentextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.jobsystem.batchjobsupport.support.AbstractDynamicWindowDeltaBatchJobItemsExtractor;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentsExtractionResult;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.sellersdocumentextraction.services.MiraklSellerDocumentsExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Handles the extraction of seller documents. It retrieves all documents from shops that
 * have been modified in Mirakl.
 */
@Component
public class SellersDocumentsExtractBatchJobItemExtractor
		extends AbstractDynamicWindowDeltaBatchJobItemsExtractor<BatchJobContext, SellersDocumentsExtractBatchJobItem> {

	private final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService;

	public SellersDocumentsExtractBatchJobItemExtractor(
			final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService,
			final BatchJobTrackingService batchJobTrackingService) {
		super(batchJobTrackingService);
		this.miraklSellerDocumentsExtractService = miraklSellerDocumentsExtractService;
	}

	/**
	 * Retrieves all the seller documents modified since the {@code delta} time and
	 * returns them as a {@link Collection} of {@link SellersDocumentsExtractBatchJobItem}
	 * @param delta the cut-out {@link Date}
	 * @return a {@link Collection} of {@link SellersDocumentsExtractBatchJobItem}
	 */
	@Override
	protected Collection<SellersDocumentsExtractBatchJobItem> getItems(final BatchJobContext ctx, final Date delta) {
		//@formatter:off
		final KYCDocumentsExtractionResult<KYCDocumentSellerInfoModel> kycDocumentsExtractionResult =
				miraklSellerDocumentsExtractService.extractProofOfIdentityAndBusinessSellerDocuments(delta);
		ctx.setPartialItemExtraction(kycDocumentsExtractionResult.hasFailed());
		ctx.setNumberOfItemsNotSuccessfullyExtracted(kycDocumentsExtractionResult.getNumberOfFailures());

		return kycDocumentsExtractionResult.getExtractedDocuments().stream()
				.map(SellersDocumentsExtractBatchJobItem::new)
				.collect(Collectors.toList());
		//@formatter:on
	}

}
