package com.paypal.kyc.converter;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HyperWalletObjectToKYCUserDocumentFlagsNotificationBodyModelConverterTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final HyperwalletUser.ProfileType PROFILE_TYPE = HyperwalletUser.ProfileType.BUSINESS;

	private static final HyperwalletUser.VerificationStatus VERIFICATION_STATUS = HyperwalletUser.VerificationStatus.REQUIRED;

	private static final String USER_TOKEN = "token";

	@InjectMocks
	private HyperWalletObjectToKYCUserDocumentFlagsNotificationBodyModelConverter testObj;

	@Test
	void convert_shouldTransformHyperWalletWebhookNotificationToKycUserDocumentFlagsNotificationModel_whenDetailsIsNotNull() {
		final var hyperWalletKycUserBodyNotification = createHyperWalletKycUserDocumentFlagsNotification();

		final var result = testObj.convert(hyperWalletKycUserBodyNotification);

		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getClientUserId()).isEqualTo(CLIENT_USER_ID);
		assertThat(result.getProfileType()).isEqualTo(PROFILE_TYPE);
		assertThat(result.getVerificationStatus().name()).isEqualTo(VERIFICATION_STATUS.name());
	}

	@Test
	void convert_shouldTransformHyperWalletWebhookNotificationToKycUserDocumentFlagsNotificationModel_whenObjectIsNotJSonObject() {
		final var result = testObj.convert(new Object());

		assertThat(result).isNull();
	}

	private Map<String, String> createHyperWalletKycUserDocumentFlagsNotification() {
		final Map<String, String> detailInfo = new HashMap<>();
		detailInfo.put("token", USER_TOKEN);
		detailInfo.put("clientUserId", CLIENT_USER_ID);
		detailInfo.put("profileType", PROFILE_TYPE.name());
		detailInfo.put("verificationStatus", VERIFICATION_STATUS.name());

		return detailInfo;
	}

}
