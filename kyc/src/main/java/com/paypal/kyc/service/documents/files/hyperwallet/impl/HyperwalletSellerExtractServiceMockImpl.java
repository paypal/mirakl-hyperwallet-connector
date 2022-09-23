package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletDocumentUploadService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl.KYCDocumentInfoToHWVerificationDocumentExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Class that mocks behaviour of sending seller KYC documents to mockserver instead of
 * sending them to HW
 */
@Profile({ "qa" })
@Slf4j
@Service("hyperwalletSellerExtractService")
public class HyperwalletSellerExtractServiceMockImpl extends HyperwalletSellerExtractServiceImpl {

	public HyperwalletSellerExtractServiceMockImpl(final HyperwalletSDKUserService hyperwalletSDKUserService,
			@Qualifier("hyperwalletDocumentUploadServiceMock") final HyperwalletDocumentUploadService hyperwalletDocumentUploadService,
			final KYCDocumentInfoToHWVerificationDocumentExecutor kycDocumentInfoToHWVerificationDocumentExecutor) {
		super(hyperwalletSDKUserService, hyperwalletDocumentUploadService,
				kycDocumentInfoToHWVerificationDocumentExecutor);
	}

}
