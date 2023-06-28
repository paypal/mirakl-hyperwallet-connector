package com.paypal.kyc.stakeholdersdocumentextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.jobsystem.batchjobsupport.support.AbstractDynamicWindowDeltaBatchJobItemsExtractor;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentsExtractionResult;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.stakeholdersdocumentextraction.services.MiraklBusinessStakeholderDocumentsExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Handles the extraction of business stakeholders documents. It retrieves all documents
 * from shops that have been modified in Mirakl.
 */
@Component
public class BusinessStakeholdersDocumentsExtractBatchJobItemExtractor extends
		AbstractDynamicWindowDeltaBatchJobItemsExtractor<BatchJobContext, BusinessStakeholdersDocumentsExtractBatchJobItem> {

	private final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService;

	public BusinessStakeholdersDocumentsExtractBatchJobItemExtractor(
			final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService,
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
	protected Collection<BusinessStakeholdersDocumentsExtractBatchJobItem> getItems(final BatchJobContext ctx,
			final Date delta) {
		//@formatter:off
		final KYCDocumentsExtractionResult<KYCDocumentBusinessStakeHolderInfoModel> kycDocumentsExtractionResult =
				miraklBusinessStakeholderDocumentsExtractService.extractBusinessStakeholderDocuments(delta);
		ctx.setPartialItemExtraction(kycDocumentsExtractionResult.hasFailed());
		ctx.setNumberOfItemsNotSuccessfullyExtracted(kycDocumentsExtractionResult.getNumberOfFailures());

		return kycDocumentsExtractionResult.getExtractedDocuments().stream()
				.map(BusinessStakeholdersDocumentsExtractBatchJobItem::new)
				.collect(Collectors.toList());
		//@formatter:on
	}

}
