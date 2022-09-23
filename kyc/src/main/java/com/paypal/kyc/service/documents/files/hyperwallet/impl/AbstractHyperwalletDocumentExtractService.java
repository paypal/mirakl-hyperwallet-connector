package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.kyc.model.KYCDocumentInfoModel;
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

	private final HyperwalletSDKUserService hyperwalletSDKUserService;

	private final HyperwalletDocumentUploadService hyperwalletDocumentUploadService;

	protected AbstractHyperwalletDocumentExtractService(final HyperwalletSDKUserService hyperwalletSDKUserService,
			final HyperwalletDocumentUploadService hyperwalletDocumentUploadService) {
		this.hyperwalletSDKUserService = hyperwalletSDKUserService;
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

	protected HyperwalletSDKUserService getHyperwalletSDKService() {
		return hyperwalletSDKUserService;
	}

}
