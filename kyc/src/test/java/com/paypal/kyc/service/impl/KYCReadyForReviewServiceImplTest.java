package com.paypal.kyc.service.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KYCReadyForReviewServiceImplTest {

	private static final String SHOP_ID = "2000";

	private static final String USER_TOKEN_1 = "userToken1";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@Spy
	@InjectMocks
	private KYCReadyForReviewServiceImpl testObj;

	@Mock
	private HyperwalletSDKUserService hyperwalletSDKUserServiceMock;

	@Mock
	private MailNotificationUtil kycMailNotificationUtilMock;

	@Mock
	private HyperwalletUser hyperwalletUserMock;

	@Mock
	private Hyperwallet hyperwalletApiClientMock;

	@Captor
	private ArgumentCaptor<HyperwalletUser> hyperwalletUserCaptor;

	@RegisterExtension
	LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.ERROR)
			.recordForType(KYCReadyForReviewServiceImpl.class);

	@Test
	void notifyReadyForReview_shouldRunNotifyBusinessStakeholderToHW() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.hyperwalletProgram(HYPERWALLET_PROGRAM)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN_1)))
				.build();
		//@formatter:on

		when(hyperwalletApiClientMock.updateUser(Mockito.any(HyperwalletUser.class))).thenReturn(hyperwalletUserMock);
		when(hyperwalletUserMock.getClientUserId()).thenReturn(SHOP_ID);
		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		testObj.notifyReadyForReview(kycDocumentOne);

		verify(hyperwalletApiClientMock).updateUser(hyperwalletUserCaptor.capture());

		final HyperwalletUser hyperwalletUser = hyperwalletUserCaptor.getValue();

		assertThat(hyperwalletUser.getToken()).isEqualTo(USER_TOKEN_1);
		assertThat(hyperwalletUser.getBusinessStakeholderVerificationStatus())
				.isEqualTo(HyperwalletUser.BusinessStakeholderVerificationStatus.READY_FOR_REVIEW);
	}

	@Test
	void notifyBstkReadyForReview_shouldSendAnEmailWhenThereIsAnErrorNotifyingToHW() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentSent = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId(SHOP_ID)
				.hyperwalletProgram(HYPERWALLET_PROGRAM)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN_1)))
				.build();
		//@formatter:on

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);
		final HyperwalletException hwException = new HyperwalletException("Something bad happened");
		doThrow(hwException).when(hyperwalletApiClientMock).updateUser(Mockito.any(HyperwalletUser.class));

		assertThatThrownBy(() -> testObj.notifyReadyForReview(kycDocumentSent))
				.isInstanceOf(HMCHyperwalletAPIException.class);

		verify(hyperwalletSDKUserServiceMock).getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM);
		verify(hyperwalletApiClientMock).updateUser(hyperwalletUserCaptor.capture());
		verify(testObj).notifyReadyForReview(kycDocumentSent);
		verify(kycMailNotificationUtilMock).sendPlainTextEmail("Issue in Hyperwallet status notification",
				String.format(
						"There was an error notifying Hyperwallet all documents were sent for shop Id [2000], so Hyperwallet will not be notified about this new situation%n%s",
						HyperwalletLoggingErrorsUtil.stringify(hwException)));

		assertThat(logTrackerStub.contains("Error notifying to Hyperwallet that all documents were sent")).isTrue();
	}

	@Test
	void notifyBstk_shouldShowLogSayingClientIdHasNoHyperwalletProgramAssigned() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId("2000")
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN_1)))
				.build();
		//@formatter:on

		testObj.notifyReadyForReview(kycDocumentOne);

		assertThat(logTrackerStub.contains("Seller with shop Id [2000] has no Hyperwallet Program")).isTrue();
	}

}
