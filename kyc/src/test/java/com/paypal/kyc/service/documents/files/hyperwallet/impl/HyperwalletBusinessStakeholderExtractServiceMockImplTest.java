package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperwalletBusinessStakeholderExtractServiceMockImplTest {

	private static final String SHOP_ID = "2000";

	private static final String USER_TOKEN = "userToken";

	private static final int BUSINESS_STAKEHOLDER_NUMBER = 1;

	private static final String BUSINESS_STAKEHOLDER_TOKEN = "bstToken";

	private static final String MOCK_SERVER_URL = "https://mockserver.aws.e2y.io";

	private static final String HYPERWALLET_PUSH_DOCUMENTS = "/hyperwallet/v4/userToken/bstToken/documents";

	private static final String HW_STAKEHOLDER_PROOF_IDENTITY_TYPE_1 = "hw-stakeholder-proof-identity-type-1";

	@Spy
	@InjectMocks
	private HyperwalletBusinessStakeholderExtractServiceMockImpl testObj;

	@Mock
	private RestTemplate restTemplateMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Test
	void execute_shouldRunPushDocumentsToMockServer() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId(SHOP_ID)
				.token(BUSINESS_STAKEHOLDER_TOKEN)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
				.businessStakeholderMiraklNumber(BUSINESS_STAKEHOLDER_NUMBER)
				.proofOfIdentity(
						List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
								HW_STAKEHOLDER_PROOF_IDENTITY_TYPE_1, "GOVERNMENT_ID")),
						BUSINESS_STAKEHOLDER_NUMBER)
				.build();
		//@formatter:on

		HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setCategory("category");
		hyperwalletVerificationDocument.setCountry("ES");
		hyperwalletVerificationDocument.setType("type");
		hyperwalletVerificationDocument.setUploadFiles(Map.of("key1", "value1"));

		when(testObj.getMockServerUrl()).thenReturn(MOCK_SERVER_URL);

		final Map<KYCDocumentBusinessStakeHolderInfoModel, List<HyperwalletVerificationDocument>> kycDocumentOne1 = Map
				.of(kycDocumentOne, List.of(hyperwalletVerificationDocument));

		testObj.callHyperwalletAPI(kycDocumentOne1.entrySet().iterator().next());

		verify(restTemplateMock).postForObject(eq(MOCK_SERVER_URL + HYPERWALLET_PUSH_DOCUMENTS), Mockito.any(),
				eq(Object.class));
	}

	@Test
	void execute_shouldSendEmailNotificationWhenFailingFilesAreIncluded() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId(SHOP_ID).token(BUSINESS_STAKEHOLDER_TOKEN)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
				.businessStakeholderMiraklNumber(BUSINESS_STAKEHOLDER_NUMBER)
				.proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						HW_STAKEHOLDER_PROOF_IDENTITY_TYPE_1, "GOVERNMENT_ID")), BUSINESS_STAKEHOLDER_NUMBER)
				.build();

		HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setCategory("category");
		hyperwalletVerificationDocument.setCountry("ES");
		hyperwalletVerificationDocument.setType("type");
		hyperwalletVerificationDocument.setUploadFiles(Map.of("key1", "value1", "fail", "failFile"));

		final Map<KYCDocumentBusinessStakeHolderInfoModel, List<HyperwalletVerificationDocument>> kycDocumentOne1 = Map
				.of(kycDocumentOne, List.of(hyperwalletVerificationDocument));

		final Map.Entry<KYCDocumentBusinessStakeHolderInfoModel, List<HyperwalletVerificationDocument>> entry = kycDocumentOne1
				.entrySet().stream().findAny().get();

		testObj.callHyperwalletAPI(entry);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
				String.format(
						"Something went wrong pushing documents to Hyperwallet for shop Id [%s] and business stakeholder number [%s]%n%s",
						SHOP_ID, BUSINESS_STAKEHOLDER_NUMBER,
						HyperwalletLoggingErrorsUtil.stringify(new HyperwalletException("Something bad happened"))));
	}

}
