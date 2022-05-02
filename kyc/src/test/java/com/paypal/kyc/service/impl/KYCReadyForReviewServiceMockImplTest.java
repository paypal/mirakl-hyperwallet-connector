package com.paypal.kyc.service.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KYCReadyForReviewServiceMockImplTest {

	private static final String SHOP_ID = "2000";

	private static final String USER_TOKEN = "userToken";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String MOCK_SERVER_URL = "https://mock.server.url";

	private static final String HYPERWALLET_NOTIFY_USER = "/hyperwallet/v4/userToken";

	@Spy
	@InjectMocks
	private KYCReadyForReviewServiceMockImpl testObj;

	@Mock
	private RestTemplate restTemplateMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@RegisterExtension
	LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.ERROR)
			.recordForType(KYCReadyForReviewServiceMockImpl.class);

	@Test
	void notifyReadyForReview_whenBusinessStakeholderIsCorrectlyFilled_shouldCallToNotifyBstk() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
				.hyperwalletProgram(HYPERWALLET_PROGRAM)
				.build();

		doNothing().when(testObj).notifyReadyForReview(kycDocumentOne);

		testObj.notifyReadyForReview(kycDocumentOne);

		verify(testObj).notifyReadyForReview(kycDocumentOne);
	}

	@Test
	void notifyBstkReadyForReview_shouldRunNotifyBusinessStakeholderToMockServer() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId(SHOP_ID)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
				.build();
		//@formatter:on

		when(testObj.getMockServerUrl()).thenReturn(MOCK_SERVER_URL);

		testObj.notifyReadyForReview(kycDocumentOne);

		verify(restTemplateMock).put(eq(MOCK_SERVER_URL + HYPERWALLET_NOTIFY_USER), any(), eq(Object.class));
	}

	@Test
	void notifyBstkReadyForReview_whenThereIsAnErrorNotifyingToHW_shouldSendAnEmail() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId(SHOP_ID)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USER_TOKEN)))
				.build();
		//@formatter:on

		HyperwalletException hwException = new HyperwalletException("Something bad happened");
		doThrow(hwException).when(restTemplateMock).put(any(String.class), any(String.class), any(Object.class));

		assertThatThrownBy(() -> testObj.notifyReadyForReview(kycDocumentOne))
				.isInstanceOf(HMCHyperwalletAPIException.class);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue in Hyperwallet status notification", String.format(
				"There was an error notifying Hyperwallet all documents were sent for shop Id [2000], so Hyperwallet will not be notified about this new situation%n%s",
				HyperwalletLoggingErrorsUtil.stringify(hwException)));

		assertThat(logTrackerStub.contains("Error notifying to Hyperwallet that all documents were sent")).isTrue();
	}

}
