package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletDocumentUploadService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl.KYCDocumentInfoToHWVerificationDocumentExecutor;
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
	private HyperwalletSDKUserService hyperwalletSDKUserServiceMock;

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
