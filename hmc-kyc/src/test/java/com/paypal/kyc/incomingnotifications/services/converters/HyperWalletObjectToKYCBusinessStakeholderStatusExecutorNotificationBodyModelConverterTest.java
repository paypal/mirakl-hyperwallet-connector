package com.paypal.kyc.incomingnotifications.services.converters;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.incomingnotifications.services.converters.HyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverter;
import com.paypal.kyc.incomingnotifications.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperWalletObjectToKYCBusinessStakeholderStatusExecutorNotificationBodyModelConverterTest {

	private static final String USER_TOKEN = "userToken";

	private static final String BUSINESS_STAKEHOLDER_TOKEN = "businessStakeholderToken";

	@InjectMocks
	private HyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverter testObj;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	private static final LogTrackerStub LOG_TRACKER_STUB = LogTrackerStub.create()
			.recordForLevel(LogTracker.LogLevel.WARN)
			.recordForType(HyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverter.class);

	@Test
	void convert_shouldConvertTheRawNotificationIntoAnInternalNotificationModel() {
		createHyperWalletKycBusinessStakeholderBodyNotification();

		final KYCBusinessStakeholderStatusNotificationBodyModel result = testObj
				.convert(hyperwalletWebhookNotificationMock);

		assertThat(result.getToken()).isEqualTo(BUSINESS_STAKEHOLDER_TOKEN);
		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getProfileType()).isEqualTo(HyperwalletUser.ProfileType.INDIVIDUAL);
		assertThat(result.getVerificationStatus()).isEqualTo(HyperwalletUser.VerificationStatus.REQUIRED);
		assertThat(result.getIsBusinessContact()).isEqualTo(Boolean.FALSE);
		assertThat(result.getIsDirector()).isEqualTo(Boolean.TRUE);
		assertThat(result.getHyperwalletWebhookNotificationType())
				.isEqualTo(KYCConstants.HwWebhookNotificationType.USERS_BUSINESS_STAKEHOLDERS_CREATED);
	}

	private void createHyperWalletKycBusinessStakeholderBodyNotification() {
		final Map<String, Object> detailInfo = new HashMap<>();
		detailInfo.put("profileType", HyperwalletUser.ProfileType.INDIVIDUAL.name());
		detailInfo.put("verificationStatus", HyperwalletUser.VerificationStatus.REQUIRED.name());
		detailInfo.put("userToken", USER_TOKEN);
		detailInfo.put("token", BUSINESS_STAKEHOLDER_TOKEN);
		detailInfo.put("isDirector", Boolean.TRUE);
		detailInfo.put("isBusinessContact", Boolean.FALSE);

		when(hyperwalletWebhookNotificationMock.getObject()).thenReturn(detailInfo);
		when(hyperwalletWebhookNotificationMock.getType())
				.thenReturn(KYCConstants.HwWebhookNotificationType.USERS_BUSINESS_STAKEHOLDERS_CREATED);
	}

	@Test
	void convert_shouldReturnNullWhenInputisNotAMap() {
		final KYCBusinessStakeholderStatusNotificationBodyModel result = testObj
				.convert(hyperwalletWebhookNotificationMock);

		assertThat(result).isNull();
	}

	@Test
	void convert_shouldLogAWarningWhenInputIsNotAMap() {
		testObj.convert(hyperwalletWebhookNotificationMock);

		assertThat(LOG_TRACKER_STUB.contains("The notification body looks empty")).isTrue();
	}

}
