package com.paypal.kyc.sellersdocumentextraction.services;

import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.documentextractioncommons.services.HyperwalletDocumentUploadService;
import com.paypal.kyc.sellersdocumentextraction.services.converters.KYCDocumentInfoToHWVerificationDocumentExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HyperwalletSellerExtractServiceImplTest {

	@InjectMocks
	private HyperwalletSellerExtractServiceImpl testObj;

	@Mock
	private UserHyperwalletSDKService userHyperwalletSDKServiceMock;

	@Mock
	private HyperwalletDocumentUploadService hyperwalletDocumentUploadServiceMock;

	@Mock
	private KYCDocumentInfoToHWVerificationDocumentExecutor kycDocumentInfoToHWVerificationDocumentExecutorMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModelMock;

	@Test
	void getHyperwalletVerificationDocuments_ShouldExecuteKycDocumentInfoToHWVerificationDocumentExecutor() {

		testObj.getHyperwalletVerificationDocuments(kycDocumentSellerInfoModelMock);

		verify(kycDocumentInfoToHWVerificationDocumentExecutorMock).execute(kycDocumentSellerInfoModelMock);
	}

}
