package com.paypal.kyc.batchjobs;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.kyc.batchjobs.businessstakeholders.BusinessStakeholdersDocumentsExtractBatchJobItemProcessor;
import com.paypal.kyc.batchjobs.sellers.SellersDocumentsExtractBatchJobItemProcessor;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.service.KYCReadyForReviewService;
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

	protected AbstractDocumentsBatchJobItemProcessor(KYCReadyForReviewService kycReadyForReviewService) {
		this.kycReadyForReviewService = kycReadyForReviewService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final T jobItem) {
		boolean areDocumentsPushedToHW = pushAndFlagDocuments(jobItem);

		if (areDocumentsPushedToHW) {
			notifyReadyToReviewDocuments(jobItem.getItem());
		}

		cleanUpDocumentsFiles(jobItem.getItem());
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
