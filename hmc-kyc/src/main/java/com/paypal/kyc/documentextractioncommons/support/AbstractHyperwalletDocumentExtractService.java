package com.paypal.kyc.documentextractioncommons.support;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.documentextractioncommons.services.HyperwalletDocumentUploadService;
import com.paypal.kyc.sellersdocumentextraction.services.HyperwalletSellerExtractServiceImpl;
import com.paypal.kyc.stakeholdersdocumentextraction.services.HyperwalletBusinessStakeholderExtractServiceImpl;
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

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	private final HyperwalletDocumentUploadService hyperwalletDocumentUploadService;

	protected AbstractHyperwalletDocumentExtractService(final UserHyperwalletSDKService userHyperwalletSDKService,
			final HyperwalletDocumentUploadService hyperwalletDocumentUploadService) {
		this.userHyperwalletSDKService = userHyperwalletSDKService;
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

	protected UserHyperwalletSDKService getHyperwalletSDKService() {
		return userHyperwalletSDKService;
	}

}
