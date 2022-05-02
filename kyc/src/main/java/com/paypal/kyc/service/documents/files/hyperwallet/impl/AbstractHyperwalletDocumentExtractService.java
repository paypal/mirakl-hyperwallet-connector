package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletDocumentUploadService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Class that holds common functionality for both
 * {@link HyperwalletBusinessStakeholderExtractServiceImpl} and
 * {@link HyperwalletSellerExtractServiceImpl}
 *
 * @param <T> the job item type.
 */
@Slf4j
public abstract class AbstractHyperwalletDocumentExtractService<T extends KYCDocumentInfoModel> {

	private final HyperwalletSDKService hyperwalletSDKService;

	private final HyperwalletDocumentUploadService hyperwalletDocumentUploadService;

	protected AbstractHyperwalletDocumentExtractService(final HyperwalletSDKService hyperwalletSDKService,
			final HyperwalletDocumentUploadService hyperwalletDocumentUploadService) {
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.hyperwalletDocumentUploadService = hyperwalletDocumentUploadService;
	}

	public boolean pushDocuments(final T kycDocumentInfoModel) {

		if (!kycDocumentInfoModel.areDocumentsFilled()) {
			log.warn("Mandatory documents missing for shop with id [{}] ", kycDocumentInfoModel.getClientUserId());
			return false;
		}

		final List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments = getHyperwalletVerificationDocuments(
				kycDocumentInfoModel);

		hyperwalletDocumentUploadService.uploadDocument(kycDocumentInfoModel, hyperwalletVerificationDocuments);

		return true;
	}

	protected abstract List<HyperwalletVerificationDocument> getHyperwalletVerificationDocuments(
			T kycDocumentInfoModel);

	protected HyperwalletSDKService getHyperwalletSDKService() {
		return hyperwalletSDKService;
	}

}
