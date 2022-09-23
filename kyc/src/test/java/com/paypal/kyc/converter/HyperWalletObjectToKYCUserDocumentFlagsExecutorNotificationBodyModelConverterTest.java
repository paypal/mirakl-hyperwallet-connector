package com.paypal.kyc.converter;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperWalletObjectToKYCUserDocumentFlagsExecutorNotificationBodyModelConverterTest {

	private static final String PROGRAM = "DEFAULT";

	private static final String USER_TOKEN = "token";

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final String PROGRAM_TOKEN = "prg-1234-1234-1234";

	private static final HyperwalletUser.ProfileType PROFILE_TYPE = HyperwalletUser.ProfileType.BUSINESS;

	private static final HyperwalletUser.VerificationStatus VERIFICATION_STATUS = HyperwalletUser.VerificationStatus.REQUIRED;

	@Spy
	@InjectMocks
	private HyperWalletObjectToKYCUserDocumentFlagsNotificationBodyModelConverter testObj;

	@Mock
	private HyperwalletSDKUserService hyperwalletSDKUserServiceMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Test
	void convert_whenDetailsAreNotNull_shouldTransformHyperWalletWebhookNotificationToKycUserDocumentFlagsNotificationModel() {
		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByHyperwalletProgram(PROGRAM))
				.thenReturn(hyperwalletMock);
		doReturn(PROGRAM).when(testObj).getHyperwalletPrograms();

		final Map<String, String> hyperWalletKycUserBodyNotification = createHyperWalletKycUserDocumentFlagsNotification();

		final KYCUserDocumentFlagsNotificationBodyModel result = testObj.convert(hyperWalletKycUserBodyNotification);

		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getClientUserId()).isEqualTo(CLIENT_USER_ID);
		assertThat(result.getProfileType()).isEqualTo(PROFILE_TYPE);
		assertThat(result.getVerificationStatus().name()).isEqualTo(VERIFICATION_STATUS.name());
		assertThat(result.getHyperwalletProgram()).isEqualTo(PROGRAM);
	}

	@Test
	void convert_whenProgramIsNotFound_shouldReturnNullHyperwalletProgramInKYCUserDocumentFlagsNotificationBodyModel() {
		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByHyperwalletProgram(PROGRAM))
				.thenReturn(hyperwalletMock);
		when(hyperwalletMock.getProgram(PROGRAM_TOKEN)).thenThrow(HyperwalletException.class);
		doReturn(PROGRAM).when(testObj).getHyperwalletPrograms();

		final Map<String, String> hyperWalletKycUserBodyNotification = createHyperWalletKycUserDocumentFlagsNotification();

		final KYCUserDocumentFlagsNotificationBodyModel result = testObj.convert(hyperWalletKycUserBodyNotification);

		assertThat(result.getUserToken()).isEqualTo(USER_TOKEN);
		assertThat(result.getClientUserId()).isEqualTo(CLIENT_USER_ID);
		assertThat(result.getProfileType()).isEqualTo(PROFILE_TYPE);
		assertThat(result.getVerificationStatus().name()).isEqualTo(VERIFICATION_STATUS.name());
		assertThat(result.getHyperwalletProgram()).isNull();
	}

	@Test
	void convert_whenObjectIsNotJSonObject_shouldTransformHyperWalletWebhookNotificationToKycUserDocumentFlagsNotificationModel() {
		final KYCUserDocumentFlagsNotificationBodyModel result = testObj.convert(new Object());

		assertThat(result).isNull();
	}

	private Map<String, String> createHyperWalletKycUserDocumentFlagsNotification() {
		final Map<String, String> detailInfo = new HashMap<>();
		detailInfo.put("token", USER_TOKEN);
		detailInfo.put("clientUserId", CLIENT_USER_ID);
		detailInfo.put("profileType", PROFILE_TYPE.name());
		detailInfo.put("verificationStatus", VERIFICATION_STATUS.name());
		detailInfo.put("programToken", PROGRAM_TOKEN);

		return detailInfo;
	}

}
