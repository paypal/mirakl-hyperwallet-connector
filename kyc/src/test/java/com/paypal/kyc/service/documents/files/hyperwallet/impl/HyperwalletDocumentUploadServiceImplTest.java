package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.*;
import com.paypal.kyc.service.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperwalletDocumentUploadServiceImplTest {

	private static final String USR_TOKEN_ONE = "usr-1234564780";

	private static final String BSTK_TOKEN_ONE = "bstk-1234564780";

	private static final String CLIENT_USER_ID_ONE = "2000";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String PROOF_OF_IDENTITY_BACK_BSH1 = "hw-bsh1-proof-identity-back";

	private static final String PROOF_OF_IDENTITY_FRONT_BSH1 = "hw-bsh1-proof-identity-front";

	@InjectMocks
	private HyperwalletDocumentUploadServiceImpl testObj;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Mock
	private MailNotificationUtil kycMailNotificationUtilMock;

	@Mock
	private Hyperwallet hyperwalletApiClientMock;

	@Mock
	private File usrOneBstkOneFileFrontMock, usrOneBstkOneFileBackMock;

	@Mock
	private HyperwalletVerificationDocument usrOneBstOneFilesOneDataMock;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.ERROR)
			.recordForType(HyperwalletBusinessStakeholderExtractServiceImpl.class);

	@Test
	void uploadDocuments_shouldPushDocuments_whenBusinessStakeholderIsPassed() {
		//@formatter:off
        final KYCDocumentModel userOneBstkOneFrontDocument = KYCDocumentModel.builder()
                .documentFieldName(PROOF_OF_IDENTITY_FRONT_BSH1)
                .file(usrOneBstkOneFileFrontMock)
                .build();
        //@formatter:on

		//@formatter:off
        final KYCDocumentModel userOneBstkOneBackDocument = KYCDocumentModel.builder()
                .documentFieldName(PROOF_OF_IDENTITY_BACK_BSH1)
                .file(usrOneBstkOneFileBackMock)
                .build();
        //@formatter:on

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel userOneBstkOne = Mockito.spy(KYCDocumentBusinessStakeHolderInfoModel
                .builder()
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .businessStakeholderMiraklNumber(1)
                .token(BSTK_TOKEN_ONE)
                .requiresKYC(true)
                .userToken(USR_TOKEN_ONE)
                .clientUserId(CLIENT_USER_ID_ONE)
                .proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
                .documents(List.of(userOneBstkOneFrontDocument, userOneBstkOneBackDocument))
                .build());
        //@formatter:on

		final List<HyperwalletVerificationDocument> usrOneBstOneFilesOneDataList = List
				.of(usrOneBstOneFilesOneDataMock);
		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		testObj.uploadDocument(userOneBstkOne, usrOneBstOneFilesOneDataList);

		verify(hyperwalletApiClientMock).uploadStakeholderDocuments(USR_TOKEN_ONE, BSTK_TOKEN_ONE,
				usrOneBstOneFilesOneDataList);
	}

	@Test
	void uploadDocuments_shouldPushDocuments_whenSellerIsPassed() {
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOneStub = spy(
				KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID_ONE)
						.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
								KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN_ONE)))
						.hyperwalletProgram(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
								KYCConstants.HW_PROGRAM, HYPERWALLET_PROGRAM)))
						.build());
		//@formatter:on

		final List<HyperwalletVerificationDocument> sellerOneBstOneFilesOneDataList = List
				.of(usrOneBstOneFilesOneDataMock);
		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		testObj.uploadDocument(kycDocumentSellerInfoModelOneStub, sellerOneBstOneFilesOneDataList);

		verify(hyperwalletApiClientMock).uploadUserDocuments(USR_TOKEN_ONE, sellerOneBstOneFilesOneDataList);
	}

	@Test
	void uploadDocumentsDocuments_whenHyperwalletAPIFails_shouldThrowException() {
		//@formatter:off
		final KYCDocumentModel userOneBstkOneFrontDocument = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_FRONT_BSH1)
				.file(usrOneBstkOneFileFrontMock)
				.build();
		//@formatter:on

		//@formatter:off
		final KYCDocumentModel userOneBstkOneBackDocument = KYCDocumentModel.builder()
				.documentFieldName(PROOF_OF_IDENTITY_BACK_BSH1)
				.file(usrOneBstkOneFileBackMock)
				.build();
		//@formatter:on

		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel userOneBstkOne = Mockito.spy(KYCDocumentBusinessStakeHolderInfoModel
				.builder()
				.hyperwalletProgram(HYPERWALLET_PROGRAM)
				.businessStakeholderMiraklNumber(1)
				.token("stk-1")
				.requiresKYC(true)
				.userToken("usr-1")
				.clientUserId("2000")
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.documents(List.of(userOneBstkOneFrontDocument, userOneBstkOneBackDocument))
				.build());
		//@formatter:on

		final List<HyperwalletVerificationDocument> usrOneBstOneFilesOneDataList = List
				.of(usrOneBstOneFilesOneDataMock);
		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final HyperwalletException expectedException = new HyperwalletException("Something went wrong");
		when(hyperwalletApiClientMock.uploadStakeholderDocuments("usr-1", "stk-1", usrOneBstOneFilesOneDataList))
				.thenThrow(expectedException);

		assertThatThrownBy(() -> testObj.uploadDocument(userOneBstkOne, usrOneBstOneFilesOneDataList))
				.isInstanceOf(HMCHyperwalletAPIException.class);

		verify(hyperwalletApiClientMock).uploadStakeholderDocuments("usr-1", "stk-1", usrOneBstOneFilesOneDataList);
		verify(kycMailNotificationUtilMock).sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
				String.format(
						"Something went wrong pushing documents to Hyperwallet for Shop Id [2000] and Business Stakeholder number [1]%n%s",
						HyperwalletLoggingErrorsUtil.stringify(expectedException)));
	}

}
