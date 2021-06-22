package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperwalletSellerExtractServiceMockImplTest {

	@Spy
	@InjectMocks
	private HyperwalletSellerExtractServiceMockImpl testObj;

	@Mock
	private RestTemplate restTemplateMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	private static final String MOCK_SERVER_URL = "https://mockserver.aws.e2y.io";

	private static final String HYPERWALLET_PUSH_DOCUMENTS = "/hyperwallet/v4/userToken/documents";

	private static final String USER_TOKEN = "userToken";

	private static final String SHOP_ID = "2000";

	@Test
	void execute_shouldRunPushDocumentsToMockServer() {
		final KYCDocumentSellerInfoModel kycDocumentOne = KYCDocumentSellerInfoModel.builder()
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
				.proofOfAddress(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD, "BANK_STATEMENT")))
				.build();

		HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setCategory("category");
		hyperwalletVerificationDocument.setCountry("ES");
		hyperwalletVerificationDocument.setType("type");
		hyperwalletVerificationDocument.setUploadFiles(Map.of("key1", "value1"));

		when(testObj.getMockServerUrl()).thenReturn(MOCK_SERVER_URL);

		final Map<KYCDocumentSellerInfoModel, List<HyperwalletVerificationDocument>> kycDocumentOne1 = Map
				.of(kycDocumentOne, List.of(hyperwalletVerificationDocument));

		testObj.callHyperwalletAPI(kycDocumentOne1.entrySet().iterator().next());

		verify(restTemplateMock).postForObject(eq(MOCK_SERVER_URL + HYPERWALLET_PUSH_DOCUMENTS), Mockito.any(),
				eq(Object.class));
	}

	@Test
	void execute_shouldSendEmailNotificationWhenFailingFilesAreIncluded() {
		final KYCDocumentSellerInfoModel kycDocumentOne = KYCDocumentSellerInfoModel.builder()
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
				.clientUserId(SHOP_ID)
				.proofOfAddress(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD, "BANK_STATEMENT")))
				.build();

		HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setCategory("category");
		hyperwalletVerificationDocument.setCountry("ES");
		hyperwalletVerificationDocument.setType("type");
		hyperwalletVerificationDocument.setUploadFiles(Map.of("key1", "value1", "fail", "failFile"));

		final Map<KYCDocumentSellerInfoModel, List<HyperwalletVerificationDocument>> kycDocumentOne1 = Map
				.of(kycDocumentOne, List.of(hyperwalletVerificationDocument));

		final Map.Entry<KYCDocumentSellerInfoModel, List<HyperwalletVerificationDocument>> entry = kycDocumentOne1
				.entrySet().stream().findAny().get();

		testObj.callHyperwalletAPI(entry);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
				String.format("Something went wrong pushing documents to Hyperwallet for shop Id [%s]%n%s",
						String.join(",", SHOP_ID),
						HyperwalletLoggingErrorsUtil.stringify(new HyperwalletException("Something bad happened"))));
	}

}
