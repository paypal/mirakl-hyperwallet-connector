package com.paypal.kyc.documentextractioncommons.support;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJobItemProcessor;
import com.paypal.kyc.sellersdocumentextraction.batchjobs.SellersDocumentsExtractBatchJobItemProcessor;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentModel;
import com.paypal.kyc.documentextractioncommons.services.KYCReadyForReviewService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;

/**
 * Class that holds common functionality for both
 * {@link BusinessStakeholdersDocumentsExtractBatchJobItemProcessor} and
 * {@link SellersDocumentsExtractBatchJobItemProcessor}
 *
 * @param <T> the job item type.
 */
@Slf4j
public abstract class AbstractDocumentsBatchJobItemProcessor<T extends BatchJobItem<? extends KYCDocumentInfoModel>>
		implements BatchJobItemProcessor<BatchJobContext, T> {

	private final KYCReadyForReviewService kycReadyForReviewService;

	protected AbstractDocumentsBatchJobItemProcessor(final KYCReadyForReviewService kycReadyForReviewService) {
		this.kycReadyForReviewService = kycReadyForReviewService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final T jobItem) {
		try {
			final boolean areDocumentsPushedToHW = pushAndFlagDocuments(jobItem);

			if (areDocumentsPushedToHW) {
				notifyReadyToReviewDocuments(jobItem.getItem());
			}
		}
		finally {
			cleanUpDocumentsFiles(jobItem.getItem());
		}
	}

	protected abstract boolean pushAndFlagDocuments(T jobItem);

	private void notifyReadyToReviewDocuments(final KYCDocumentInfoModel kycDocumentInfoModel) {
		getKycReadyForReviewService().notifyReadyForReview(kycDocumentInfoModel);
	}

	@SuppressWarnings("java:S3864")
	protected <R extends KYCDocumentInfoModel> void cleanUpDocumentsFiles(final R successFullPushedDocument) {
		log.info("Cleaning up files from disk...");
		//@formatter:off
		successFullPushedDocument.getDocuments().stream()
				.map(KYCDocumentModel::getFile)
				.filter(Objects::nonNull)
				.peek(file -> log.info("File selected to be deleted [{}]", file.getAbsolutePath()))
				.forEach(File::delete);
		//@formatter:on
		log.info("Cleaning up done successfully!");
	}

	protected KYCReadyForReviewService getKycReadyForReviewService() {
		return kycReadyForReviewService;
	}

}
