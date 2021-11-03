package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.model.KYCProofOfIdentityEnum;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.businessstakeholder.impl.KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder.VerificationStatus.REQUIRED;
import static com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder.VerificationStatus.VERIFIED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperwalletBusinessStakeholderExtractServiceImplTest {

	private static final String PROOF_OF_IDENTITY_FRONT_BSH1 = "hw-bsh1-proof-identity-front";

	private static final String PROOF_OF_IDENTITY_BACK_BSH1 = "hw-bsh1-proof-identity-back";

	private static final String PROOF_OF_IDENTITY_FRONT_BSH3 = "hw-bsh3-proof-identity-front";

	private static final String SHOP_ID = "2000";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String USER_TOKEN = "userToken";

	private static final String BUSINESS_STAKE_HOLDER_ONE_TOKEN = "businessStakeHolderOneToken";

	@Spy
	@InjectMocks
	private HyperwalletBusinessStakeholderExtractServiceImpl testObj;

	@Mock
	private KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutor businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock;

	@Mock
	private MailNotificationUtil kycMailNotificationUtilMock;

	@Mock
	private HyperwalletBusinessStakeholder businessStakeHolderRequiredVerificationMock, businessStakeHolderVerifiedMock;

	@Mock
	private HyperwalletList<HyperwalletBusinessStakeholder> businessStakeholders;

	@Mock
	private Hyperwallet hyperwalletApiClientMock;

	@Mock
	private File usrOneBstkOneFileFrontMock, userOneBstkThreeFileFrontMock, usrOneBstkOneFileBackMock,
			userTwoBstkThreeFileFrontMock;

	@Mock
	private HyperwalletVerificationDocument usrOneBstOneFilesOneDataMock, usrOneBstThreeFilesDataMock,
			usrTwoBstThreeFilesDataMock;

	@RegisterExtension
	LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.ERROR)
			.recordForType(HyperwalletBusinessStakeholderExtractServiceImpl.class);

	@Mock
	private HyperwalletUser hyperwalletUserMock;

	@Captor
	private ArgumentCaptor<HyperwalletUser> hyperwalletUserCaptor;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Test
	void getKYCRequiredVerificationBusinessStakeHolders_shouldReturnEmptyListWhenNoRequiredVerificationBusinessStakeholdersIsReceived() {

		when(businessStakeHolderRequiredVerificationMock.getVerificationStatus()).thenReturn(VERIFIED);

		when(businessStakeholders.getData()).thenReturn(List.of(businessStakeHolderRequiredVerificationMock));
		when(hyperwalletApiClientMock.listBusinessStakeholders(USER_TOKEN)).thenReturn(businessStakeholders);
		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final List<String> result = testObj.getKYCRequiredVerificationBusinessStakeHolders(HYPERWALLET_PROGRAM,
				USER_TOKEN);

		verify(hyperwalletSDKServiceMock).getHyperwalletInstance(HYPERWALLET_PROGRAM);
		assertThat(result).isEmpty();
	}

	@Test
	void getKYCRequiredVerificationBusinessStakeHolders_shouldReturnRequiredVerificationBusinessStakeHolders() {

		when(businessStakeHolderRequiredVerificationMock.getToken()).thenReturn(BUSINESS_STAKE_HOLDER_ONE_TOKEN);
		when(businessStakeHolderRequiredVerificationMock.getVerificationStatus()).thenReturn(REQUIRED);
		when(businessStakeHolderVerifiedMock.getVerificationStatus()).thenReturn(VERIFIED);

		when(businessStakeholders.getData())
				.thenReturn(List.of(businessStakeHolderRequiredVerificationMock, businessStakeHolderVerifiedMock));
		when(hyperwalletApiClientMock.listBusinessStakeholders(USER_TOKEN)).thenReturn(businessStakeholders);
		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final List<String> result = testObj.getKYCRequiredVerificationBusinessStakeHolders(HYPERWALLET_PROGRAM,
				USER_TOKEN);

		verify(hyperwalletSDKServiceMock).getHyperwalletInstance(HYPERWALLET_PROGRAM);
		assertThat(result).containsExactlyInAnyOrder(BUSINESS_STAKE_HOLDER_ONE_TOKEN);

	}

	@Test
	void pushBusinessStakeholderDocuments_shouldPushDocumentsForBusinessStakeholdersWhenAllDocumentsAreFulfilledByBusinessStakeholder() {
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
        final KYCDocumentModel userOneBstkThreeFrontDocument = KYCDocumentModel.builder()
                .documentFieldName(PROOF_OF_IDENTITY_FRONT_BSH3)
                .file(userOneBstkThreeFileFrontMock)
                .build();
        //@formatter:on

		//@formatter:off
        final KYCDocumentModel userTwoBstkOneFrontDocument = KYCDocumentModel.builder()
                .documentFieldName(PROOF_OF_IDENTITY_FRONT_BSH1)
                .file(userTwoBstkThreeFileFrontMock)
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

		final KYCDocumentBusinessStakeHolderInfoModel userOneBstkOneSentToHW = userOneBstkOne.toBuilder()
				.sentToHyperwallet(Boolean.TRUE).build();

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel userOneBstkThree = Mockito.spy(KYCDocumentBusinessStakeHolderInfoModel
                .builder()
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .businessStakeholderMiraklNumber(3)
                .token("stk-3")
                .requiresKYC(true)
                .userToken("usr-1")
                .clientUserId("2000")
                .proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
                .documents(List.of(userOneBstkThreeFrontDocument))
                .build());
        //@formatter:on

		final KYCDocumentBusinessStakeHolderInfoModel userOneBstkThreeSentToHW = userOneBstkThree.toBuilder()
				.sentToHyperwallet(Boolean.TRUE).build();

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel userTwoBstkThree = Mockito.spy(KYCDocumentBusinessStakeHolderInfoModel
                .builder()
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .businessStakeholderMiraklNumber(3)
                .token("stk-3")
                .requiresKYC(true)
                .userToken("usr-2")
                .clientUserId("2001")
                .proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
                .documents(List
                        .of(userTwoBstkOneFrontDocument))
                .build());
        //@formatter:on

		final KYCDocumentBusinessStakeHolderInfoModel userTwoBstkThreeSentToHW = userTwoBstkThree.toBuilder()
				.sentToHyperwallet(Boolean.TRUE).build();

		when(userOneBstkOne.areDocumentsFilled()).thenReturn(true);
		when(userOneBstkThree.areDocumentsFilled()).thenReturn(true);
		when(userTwoBstkThree.areDocumentsFilled()).thenReturn(true);

		final List<HyperwalletVerificationDocument> usrOneBstOneFilesOneDataList = List
				.of(usrOneBstOneFilesOneDataMock);
		when(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock
				.execute(userOneBstkOne)).thenReturn(usrOneBstOneFilesOneDataList);
		final List<HyperwalletVerificationDocument> usrOneBstThreeFilesDataList = List.of(usrOneBstThreeFilesDataMock);
		when(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock
				.execute(userOneBstkThree)).thenReturn(usrOneBstThreeFilesDataList);
		final List<HyperwalletVerificationDocument> usrTwoBstOneFilesDataList = List.of(usrTwoBstThreeFilesDataMock);
		when(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock
				.execute(userTwoBstkThree)).thenReturn(usrTwoBstOneFilesDataList);
		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.pushBusinessStakeholderDocuments(List.of(userOneBstkOne, userOneBstkThree, userTwoBstkThree));

		verify(hyperwalletApiClientMock).uploadStakeholderDocuments("usr-1", "stk-1", usrOneBstOneFilesOneDataList);
		verify(hyperwalletApiClientMock).uploadStakeholderDocuments("usr-1", "stk-3", usrOneBstThreeFilesDataList);
		verify(hyperwalletApiClientMock).uploadStakeholderDocuments("usr-2", "stk-3", usrTwoBstOneFilesDataList);

		assertThat(result).containsExactlyInAnyOrder(userOneBstkOneSentToHW, userOneBstkThreeSentToHW,
				userTwoBstkThreeSentToHW);
	}

	@Test
	void pushBusinessStakeholderDocuments_shouldReturnAnEmptyListWhenEmptyListIsProvided() {
		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.pushBusinessStakeholderDocuments(List.of());

		assertThat(result).isEmpty();
		verifyNoInteractions(hyperwalletApiClientMock);
		verifyNoInteractions(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock);
	}

	@Test
	void pushBusinessStakeholderDocuments_shouldSkipBusinessStakeholdersWithNotAllDocumentsFulfilled() {
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
        final KYCDocumentModel userOneBstkThreeFrontDocument = KYCDocumentModel.builder()
                .documentFieldName(PROOF_OF_IDENTITY_FRONT_BSH3)
                .file(userOneBstkThreeFileFrontMock)
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

		final KYCDocumentBusinessStakeHolderInfoModel userOneBstkOneSentToHW = userOneBstkOne.toBuilder()
				.sentToHyperwallet(Boolean.TRUE).build();

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel userOneBstkThree = Mockito.spy(KYCDocumentBusinessStakeHolderInfoModel
                .builder()
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .businessStakeholderMiraklNumber(3)
                .token("stk-3")
                .requiresKYC(true)
                .userToken("usr-1")
                .clientUserId("2000")
                .proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
                .documents(List.of(userOneBstkThreeFrontDocument))
                .build());
        //@formatter:on

		final KYCDocumentBusinessStakeHolderInfoModel userOneBstkThreeSentToHW = userOneBstkThree.toBuilder()
				.sentToHyperwallet(Boolean.TRUE).build();

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel userTwoBstkThree = Mockito.spy(KYCDocumentBusinessStakeHolderInfoModel
                .builder()
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .businessStakeholderMiraklNumber(3)
                .token("stk-3")
                .requiresKYC(true)
                .userToken("usr-2")
                .clientUserId("2001")
                .proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
                .build());
        //@formatter:on

		when(userOneBstkOne.areDocumentsFilled()).thenReturn(true);
		when(userOneBstkThree.areDocumentsFilled()).thenReturn(true);
		when(userTwoBstkThree.areDocumentsFilled()).thenReturn(false);

		final List<HyperwalletVerificationDocument> usrOneBstOneFilesOneDataList = List
				.of(usrOneBstOneFilesOneDataMock);
		when(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock
				.execute(userOneBstkOne)).thenReturn(usrOneBstOneFilesOneDataList);
		final List<HyperwalletVerificationDocument> usrOneBstThreeFilesDataList = List.of(usrOneBstThreeFilesDataMock);
		when(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock
				.execute(userOneBstkThree)).thenReturn(usrOneBstThreeFilesDataList);
		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.pushBusinessStakeholderDocuments(List.of(userOneBstkOne, userOneBstkThree, userTwoBstkThree));

		verify(hyperwalletApiClientMock).uploadStakeholderDocuments("usr-1", "stk-1", usrOneBstOneFilesOneDataList);
		verify(hyperwalletApiClientMock).uploadStakeholderDocuments("usr-1", "stk-3", usrOneBstThreeFilesDataList);

		assertThat(result).containsExactlyInAnyOrder(userOneBstkOneSentToHW, userOneBstkThreeSentToHW);
	}

	@Test
	void pushBusinessStakeholderDocuments_shouldFailGracefullyWhenHyperwalletAPIFailsWithOneSpecificBstkFile() {
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
        final KYCDocumentModel userOneBstkThreeFrontDocument = KYCDocumentModel.builder()
                .documentFieldName(PROOF_OF_IDENTITY_FRONT_BSH3)
                .file(userOneBstkThreeFileFrontMock)
                .build();
        //@formatter:on

		//@formatter:off
        final KYCDocumentModel userTwoBstkOneFrontDocument = KYCDocumentModel.builder()
                .documentFieldName(PROOF_OF_IDENTITY_FRONT_BSH1)
                .file(userTwoBstkThreeFileFrontMock)
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

		final KYCDocumentBusinessStakeHolderInfoModel userOneBstkOneSentToHW = userOneBstkOne.toBuilder()
				.sentToHyperwallet(Boolean.TRUE).build();

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel userOneBstkThree = Mockito.spy(KYCDocumentBusinessStakeHolderInfoModel
                .builder()
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .businessStakeholderMiraklNumber(3)
                .token("stk-3")
                .requiresKYC(true)
                .userToken("usr-1")
                .clientUserId("2000")
                .proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
                .documents(List.of(userOneBstkThreeFrontDocument))
                .build());
        //@formatter:on

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel userTwoBstkThree = Mockito.spy(KYCDocumentBusinessStakeHolderInfoModel
                .builder()
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .businessStakeholderMiraklNumber(3)
                .token("stk-3")
                .requiresKYC(true)
                .userToken("usr-2")
                .clientUserId("2001")
                .proofOfIdentity(KYCProofOfIdentityEnum.PASSPORT)
                .documents(List
                        .of(userTwoBstkOneFrontDocument))
                .build());

        final KYCDocumentBusinessStakeHolderInfoModel userTwoBstkThreeSentToHW = userTwoBstkThree.toBuilder()
                .sentToHyperwallet(Boolean.TRUE).build();
        //@formatter:on

		when(userOneBstkOne.areDocumentsFilled()).thenReturn(true);
		when(userOneBstkThree.areDocumentsFilled()).thenReturn(true);
		when(userTwoBstkThree.areDocumentsFilled()).thenReturn(true);

		final List<HyperwalletVerificationDocument> usrOneBstOneFilesOneDataList = List
				.of(usrOneBstOneFilesOneDataMock);
		when(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock
				.execute(userOneBstkOne)).thenReturn(usrOneBstOneFilesOneDataList);
		final List<HyperwalletVerificationDocument> usrOneBstThreeFilesDataList = List.of(usrOneBstThreeFilesDataMock);
		when(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock
				.execute(userOneBstkThree)).thenReturn(usrOneBstThreeFilesDataList);
		final List<HyperwalletVerificationDocument> usrTwoBstOneFilesDataList = List.of(usrTwoBstThreeFilesDataMock);
		when(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock
				.execute(userTwoBstkThree)).thenReturn(usrTwoBstOneFilesDataList);
		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final HyperwalletException expectedException = new HyperwalletException("Something went wrong");
		lenient().when(
				hyperwalletApiClientMock.uploadStakeholderDocuments("usr-1", "stk-3", usrOneBstThreeFilesDataList))
				.thenThrow(expectedException);

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.pushBusinessStakeholderDocuments(List.of(userOneBstkOne, userOneBstkThree, userTwoBstkThree));

		verify(hyperwalletApiClientMock).uploadStakeholderDocuments("usr-1", "stk-1", usrOneBstOneFilesOneDataList);
		verify(hyperwalletApiClientMock).uploadStakeholderDocuments("usr-1", "stk-3", usrOneBstThreeFilesDataList);
		verify(hyperwalletApiClientMock).uploadStakeholderDocuments("usr-2", "stk-3", usrTwoBstOneFilesDataList);
		verify(kycMailNotificationUtilMock).sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
				String.format(
						"Something went wrong pushing documents to Hyperwallet for shop Id [2000] and business stakeholder number [3]%n%s",
						HyperwalletLoggingErrorsUtil.stringify(expectedException)));

		assertThat(result).containsExactlyInAnyOrder(userOneBstkOneSentToHW, userOneBstkThree,
				userTwoBstkThreeSentToHW);
	}

	@Test
	void notifyAllDocumentsSentForBstk() {

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
                .userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
                        KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .sentToHyperwallet(Boolean.TRUE)
                .build();
        //@formatter:on

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOneNotified = kycDocumentOne.toBuilder()
                .notifiedDocumentsReadyForReview(Boolean.TRUE)
                .build();
        //@formatter:on

		doReturn(List.of(kycDocumentOneNotified)).when(testObj)
				.notifyBstk(Map.entry(USER_TOKEN, List.of(kycDocumentOne)));

		//@formatter:on

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.notifyAllDocumentsSentForBstk(List.of(kycDocumentOne));

		assertThat(result).containsExactly(kycDocumentOneNotified);

	}

	@Test
	void notifyBstk_shouldRunNotifyBusinessStakeholderToHW() {
		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
                        KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
                .build();
        //@formatter:on

		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOneNotified = kycDocumentOne.toBuilder()
                .notifiedDocumentsReadyForReview(Boolean.TRUE)
                .build();
        //@formatter:on

		when(hyperwalletApiClientMock.updateUser(Mockito.any(HyperwalletUser.class))).thenReturn(hyperwalletUserMock);
		when(hyperwalletUserMock.getClientUserId()).thenReturn(SHOP_ID);
		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.notifyBstk(Map.entry(USER_TOKEN, List.of(kycDocumentOne)));

		verify(hyperwalletApiClientMock).updateUser(hyperwalletUserCaptor.capture());

		final HyperwalletUser hyperwalletUser = hyperwalletUserCaptor.getValue();

		assertThat(hyperwalletUser.getToken()).isEqualTo(USER_TOKEN);
		assertThat(hyperwalletUser.getBusinessStakeholderVerificationStatus())
				.isEqualTo(HyperwalletUser.BusinessStakeholderVerificationStatus.READY_FOR_REVIEW);
		assertThat(result).containsExactly(kycDocumentOneNotified);

	}

	@Test
	void notifyBstk_shouldSendAnEmailWhenThereIsAnErrorNotifyingToHW() {
		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
                .clientUserId(SHOP_ID)
                .hyperwalletProgram(HYPERWALLET_PROGRAM)
                .userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
                        KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
                .build();
        //@formatter:on

		when(hyperwalletSDKServiceMock.getHyperwalletInstance(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);
		HyperwalletException hwException = new HyperwalletException("Something bad happened");
		doThrow(hwException).when(hyperwalletApiClientMock).updateUser(Mockito.any(HyperwalletUser.class));

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.notifyBstk(Map.entry(USER_TOKEN, List.of(kycDocumentOne)));

		verify(hyperwalletSDKServiceMock).getHyperwalletInstance(HYPERWALLET_PROGRAM);
		verify(hyperwalletApiClientMock).updateUser(hyperwalletUserCaptor.capture());
		verify(kycMailNotificationUtilMock).sendPlainTextEmail("Issue in Hyperwallet status notification",
				String.format(
						"There was an error notifying Hyperwallet all documents were sent for shop Id [2000], so Hyperwallet will not be notified about this new situation%n%s",
						HyperwalletLoggingErrorsUtil.stringify(hwException)));

		assertThat(logTrackerStub.contains("Error notifying to Hyperwallet that all documents were sent")).isTrue();
		assertThat(result).isEqualTo(List.of(kycDocumentOne));

	}

	@Test
	void notifyBstk_shouldShowLogSayingClientIdHasNoHyperwalletProgramAssigned() {
		//@formatter:off
        final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
                .clientUserId("2000")
                .userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
                        KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
                .build();
        //@formatter:on

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.notifyBstk(Map.entry(USER_TOKEN, List.of(kycDocumentOne)));

		assertThat(logTrackerStub.contains("Seller with shop Id [2000] has no Hyperwallet Program")).isTrue();
	}

}
