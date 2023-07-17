package com.paypal.kyc.sellersdocumentextraction.services;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.kyc.documentextractioncommons.services.HyperwalletDocumentUploadService;
import com.paypal.kyc.documentextractioncommons.support.AbstractHyperwalletDocumentExtractService;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.sellersdocumentextraction.services.converters.KYCDocumentInfoToHWVerificationDocumentExecutor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link HyperwalletSellerExtractService}
 */
@Slf4j
@Getter
@Service
public class HyperwalletSellerExtractServiceImpl
		extends AbstractHyperwalletDocumentExtractService<KYCDocumentSellerInfoModel>
		implements HyperwalletSellerExtractService {

	private final KYCDocumentInfoToHWVerificationDocumentExecutor kycDocumentInfoToHWVerificationDocumentExecutor;

	public HyperwalletSellerExtractServiceImpl(final UserHyperwalletSDKService userHyperwalletSDKService,
			final HyperwalletDocumentUploadService hyperwalletDocumentUploadService,
			final KYCDocumentInfoToHWVerificationDocumentExecutor kycDocumentInfoToHWVerificationDocumentExecutor) {
		super(userHyperwalletSDKService, hyperwalletDocumentUploadService);
		this.kycDocumentInfoToHWVerificationDocumentExecutor = kycDocumentInfoToHWVerificationDocumentExecutor;
	}

	@Override
	protected List<HyperwalletVerificationDocument> getHyperwalletVerificationDocuments(
			final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel) {
		return kycDocumentInfoToHWVerificationDocumentExecutor.execute(kycDocumentSellerInfoModel);
	}

}
