package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletDocumentUploadService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletSellerExtractService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl.KYCDocumentInfoToHWVerificationDocumentExecutor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link HyperwalletSellerExtractService}
 */
@Profile({ "!qa" })
@Slf4j
@Getter
@Service
public class HyperwalletSellerExtractServiceImpl
		extends AbstractHyperwalletDocumentExtractService<KYCDocumentSellerInfoModel>
		implements HyperwalletSellerExtractService {

	private final KYCDocumentInfoToHWVerificationDocumentExecutor kycDocumentInfoToHWVerificationDocumentExecutor;

	public HyperwalletSellerExtractServiceImpl(final HyperwalletSDKUserService hyperwalletSDKUserService,
			final HyperwalletDocumentUploadService hyperwalletDocumentUploadService,
			final KYCDocumentInfoToHWVerificationDocumentExecutor kycDocumentInfoToHWVerificationDocumentExecutor) {
		super(hyperwalletSDKUserService, hyperwalletDocumentUploadService);
		this.kycDocumentInfoToHWVerificationDocumentExecutor = kycDocumentInfoToHWVerificationDocumentExecutor;
	}

	@Override
	protected List<HyperwalletVerificationDocument> getHyperwalletVerificationDocuments(
			final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel) {
		return kycDocumentInfoToHWVerificationDocumentExecutor.execute(kycDocumentSellerInfoModel);
	}

}
