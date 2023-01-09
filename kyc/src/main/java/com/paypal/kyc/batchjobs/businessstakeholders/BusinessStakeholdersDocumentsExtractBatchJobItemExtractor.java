package com.paypal.kyc.batchjobs.businessstakeholders;

import com.paypal.infrastructure.batchjob.AbstractDeltaBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobTrackingService;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentsExtractionResult;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Handles the extraction of business stakeholders documents. It retrieves all documents
 * from shops that have been modified in Mirakl.
 */
@Service
public class BusinessStakeholdersDocumentsExtractBatchJobItemExtractor
		extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, BusinessStakeholdersDocumentsExtractBatchJobItem> {

	private final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService;

	public BusinessStakeholdersDocumentsExtractBatchJobItemExtractor(
			MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService,
			final BatchJobTrackingService batchJobTrackingService) {
		super(batchJobTrackingService);
		this.miraklBusinessStakeholderDocumentsExtractService = miraklBusinessStakeholderDocumentsExtractService;
	}

	/**
	 * Retrieves all the business stakeholder documents modified since the {@code delta}
	 * time and returns them as a {@link Collection} of
	 * {@link BusinessStakeholdersDocumentsExtractBatchJobItem}
	 * @param delta the cut-out {@link Date}
	 * @return a {@link Collection} of
	 * {@link BusinessStakeholdersDocumentsExtractBatchJobItem}
	 */
	@Override
	protected Collection<BusinessStakeholdersDocumentsExtractBatchJobItem> getItems(BatchJobContext ctx,
			final Date delta) {
		//@formatter:off
		KYCDocumentsExtractionResult<KYCDocumentBusinessStakeHolderInfoModel> kycDocumentsExtractionResult =
				miraklBusinessStakeholderDocumentsExtractService.extractBusinessStakeholderDocuments(delta);
		ctx.setPartialItemExtraction(kycDocumentsExtractionResult.hasFailed());
		ctx.setNumberOfItemsNotSuccessfullyExtracted(kycDocumentsExtractionResult.getNumberOfFailures());

		return kycDocumentsExtractionResult.getExtractedDocuments().stream()
				.map(BusinessStakeholdersDocumentsExtractBatchJobItem::new)
				.collect(Collectors.toList());
		//@formatter:on
	}

}
