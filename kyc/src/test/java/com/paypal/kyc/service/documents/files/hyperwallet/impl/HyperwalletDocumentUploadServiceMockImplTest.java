package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperwalletDocumentUploadServiceMockImplTest {

	private static final String MOCK_SERVER_URL = "mockServerUrl";

	private static final String HYPERWALLET_PUSH_DOCUMENTS_BSTK = "/hyperwallet/v4/{userToken}/{bstToken}/documents";

	private static final String HYPERWALLET_PUSH_DOCUMENTS_SELLER = "/hyperwallet/v4/{userToken}/documents";

	private static final String BST_USER_TOKEN = "BSTUserToken";

	private static final String BST_TOKEN = "BSTToken";

	private static final String SELLER_USER_TOKEN = "SellerUserToken";

	private static final String FAILED_FILE = "failedFile";

	private static final String NOT_FAILED_FILE = "file";

	private static final String FAILED_FILE_NAME = "failFileName";

	private static final String NOT_FAILED_FILE_NAME = "fileName";

	public static final String DOCUMENTS_REQUEST = "[{\"uploadFiles\":{\"file\":\"fileName\"}}]";

	@InjectMocks
	private HyperwalletDocumentUploadServiceMockImpl testObj;

	@Mock
	private RestTemplate restTemplatemock;

	@Mock
	private HyperwalletSDKUserService hyperwalletSDKUserServiceMock;

	@Mock
	private MailNotificationUtil kycMailNotificationUtilMock;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModelMock;

	private static List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments;

	@BeforeEach
	void setUp() {

		testObj = new HyperwalletDocumentUploadServiceMockImpl(hyperwalletSDKUserServiceMock,
				kycMailNotificationUtilMock, MOCK_SERVER_URL, restTemplatemock);
		lenient().when(kycDocumentBusinessStakeHolderInfoModelMock.getUserToken()).thenReturn(BST_USER_TOKEN);
		lenient().when(kycDocumentBusinessStakeHolderInfoModelMock.getToken()).thenReturn(BST_TOKEN);
		lenient().when(kycDocumentSellerInfoModelMock.getUserToken()).thenReturn(SELLER_USER_TOKEN);

		final HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setUploadFiles(Map.of(NOT_FAILED_FILE, NOT_FAILED_FILE_NAME));
		hyperwalletVerificationDocuments = List.of(hyperwalletVerificationDocument);
	}

	@Test
	void invokeHyperwalletAPI_ShouldThrowAnHMCHyperwalletAPIException_WhenThereAreFailingFiles() {

		final HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setUploadFiles(Map.of(FAILED_FILE, FAILED_FILE_NAME));
		final List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments = List
				.of(hyperwalletVerificationDocument);

		assertThatThrownBy(() -> testObj.uploadDocument(kycDocumentBusinessStakeHolderInfoModelMock,
				hyperwalletVerificationDocuments)).isInstanceOf(HMCHyperwalletAPIException.class);

		verifyNoInteractions(restTemplatemock);
	}

	@Test
	void invokeHyperwalletAPI_ShouldCallRestTemplateWithBSTUrl_WhenKYCDocumentInfoModelIsAKYCDocumentBusinessStakeHolderInfoModel() {

		testObj.uploadDocument(kycDocumentBusinessStakeHolderInfoModelMock, hyperwalletVerificationDocuments);

		verify(restTemplatemock).postForObject(MOCK_SERVER_URL + HYPERWALLET_PUSH_DOCUMENTS_BSTK
				.replace("{userToken}", BST_USER_TOKEN).replace("{bstToken}", BST_TOKEN), DOCUMENTS_REQUEST,
				Object.class);
	}

	@Test
	void invokeHyperwalletAPI_ShouldCallRestTemplateWithSellerUrl_WhenKYCDocumentInfoModelIsAKYCDocumentSellerInfoModel() {

		testObj.uploadDocument(kycDocumentSellerInfoModelMock, hyperwalletVerificationDocuments);

		verify(restTemplatemock).postForObject(
				MOCK_SERVER_URL + HYPERWALLET_PUSH_DOCUMENTS_SELLER.replace("{userToken}", SELLER_USER_TOKEN),
				DOCUMENTS_REQUEST, Object.class);
	}

}
