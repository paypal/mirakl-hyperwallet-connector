package com.paypal.kyc.converter;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverterTest {

	private static final String USER_TOKEN = "userToken";

	private static final String BUSINESS_STAKEHOLDER_TOKEN = "businessStakeholderToken";

	@InjectMocks
	private HyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverter testObj;

	@Test
	void convert_shouldConvertTheRawNotificationIntoAnInternalNotificationModel() {
		final KYCBusinessStakeholderStatusNotificationBodyModel result = testObj
				.convert(createHyperWalletKycBusinessStakeholderBodyNotification());

		assertThat(result.getToken()).isEqualTo(BUSINESS_STAKEHOLDER_TOKEN);
		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getProfileType()).isEqualTo(HyperwalletUser.ProfileType.INDIVIDUAL);
		assertThat(result.getVerificationStatus()).isEqualTo(HyperwalletUser.VerificationStatus.REQUIRED);
	}

	private Map<String, String> createHyperWalletKycBusinessStakeholderBodyNotification() {
		final Map<String, String> detailInfo = new HashMap<>();
		detailInfo.put("profileType", HyperwalletUser.ProfileType.INDIVIDUAL.name());
		detailInfo.put("verificationStatus", HyperwalletUser.VerificationStatus.REQUIRED.name());
		detailInfo.put("userToken", USER_TOKEN);
		detailInfo.put("token", BUSINESS_STAKEHOLDER_TOKEN);

		return detailInfo;
	}

}
