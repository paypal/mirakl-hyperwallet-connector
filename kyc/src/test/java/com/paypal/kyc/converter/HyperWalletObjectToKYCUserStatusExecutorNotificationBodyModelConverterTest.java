package com.paypal.kyc.converter;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCDocumentNotificationModel;
import com.paypal.kyc.model.KYCRejectionReasonTypeEnum;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCRejectionReasonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperWalletObjectToKYCUserStatusExecutorNotificationBodyModelConverterTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final HyperwalletUser.ProfileType BUSINESS_PROFILE_TYPE = HyperwalletUser.ProfileType.BUSINESS;

	private static final HyperwalletUser.ProfileType INDIVIDUAL_PROFILE_TYPE = HyperwalletUser.ProfileType.INDIVIDUAL;

	private static final HyperwalletUser.VerificationStatus REQUIRED_VERIFICATION_STATUS = HyperwalletUser.VerificationStatus.REQUIRED;

	private static final HyperwalletUser.BusinessStakeholderVerificationStatus NOT_REQUIRED_BUSINESS_VERIFICATION_STATUS = HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED;

	private static final HyperwalletUser.LetterOfAuthorizationStatus FAILED_LETTER_VERIFICATION_STATUS = HyperwalletUser.LetterOfAuthorizationStatus.FAILED;

	@InjectMocks
	private HyperWalletObjectToKYCUserStatusNotificationBodyModelConverter testObj;

	@Mock
	private KYCRejectionReasonService kycRejectionReasonServiceMock;

	@Mock
	private Converter<Object, List<KYCDocumentNotificationModel>> objectKYCDocumentNotificationModelListConverterMock;

	@Test
	void convert_shouldTransformHyperWalletWebhookBusinessUserNotificationToKycBusinessUserStatusNotificationModel_whenDetailsIsNotNull() {
		final Map<String, String> hyperWalletKycUserBodyNotification = createHyperWalletKycUserBodyNotification(
				BUSINESS_PROFILE_TYPE);

		when(kycRejectionReasonServiceMock.getReasonTypes(hyperWalletKycUserBodyNotification))
				.thenReturn(List.of(KYCRejectionReasonTypeEnum.VERIFICATIONSTATUS_PROF_REQUIRED));

		final KYCUserStatusNotificationBodyModel result = testObj.convert(hyperWalletKycUserBodyNotification);

		assertThat(result.getClientUserId()).isEqualTo(CLIENT_USER_ID);
		assertThat(result.getProfileType()).isEqualTo(BUSINESS_PROFILE_TYPE);
		assertThat(result.getVerificationStatus()).isEqualTo(REQUIRED_VERIFICATION_STATUS);
		assertThat(result.getBusinessStakeholderVerificationStatus())
				.isEqualTo(NOT_REQUIRED_BUSINESS_VERIFICATION_STATUS);
		assertThat(result.getLetterOfAuthorizationStatus()).isEqualTo(FAILED_LETTER_VERIFICATION_STATUS);
		assertThat(result.getReasonsType())
				.containsExactlyInAnyOrder(KYCRejectionReasonTypeEnum.VERIFICATIONSTATUS_PROF_REQUIRED);
		verify(objectKYCDocumentNotificationModelListConverterMock).convert(hyperWalletKycUserBodyNotification);
	}

	@Test
	void convert_shouldTransformHyperWalletWebhookIndividualUserNotificationToKycIndividualUserStatusNotificationModel_whenDetailsIsNotNull() {
		final Map<String, String> hyperWalletKycUserBodyNotification = createHyperWalletKycUserBodyNotification(
				INDIVIDUAL_PROFILE_TYPE);

		when(kycRejectionReasonServiceMock.getReasonTypes(hyperWalletKycUserBodyNotification))
				.thenReturn(List.of(KYCRejectionReasonTypeEnum.VERIFICATIONSTATUS_IND_REQUIRED));

		final KYCUserStatusNotificationBodyModel result = testObj.convert(hyperWalletKycUserBodyNotification);

		assertThat(result.getClientUserId()).isEqualTo(CLIENT_USER_ID);
		assertThat(result.getProfileType()).isEqualTo(INDIVIDUAL_PROFILE_TYPE);
		assertThat(result.getVerificationStatus().name()).isEqualTo(REQUIRED_VERIFICATION_STATUS.name());
		assertThat(result.getReasonsType()).containsExactly(KYCRejectionReasonTypeEnum.VERIFICATIONSTATUS_IND_REQUIRED);
	}

	@Test
	void convert_shouldTransformHyperWalletWebhookUserNotificationWithBusinessStakeHolderRequirementToKycUserStatusNotificationModelWithBusinessStakeholderReasonType_whenDetailsIsNotNull() {
		final Map<String, String> hyperWalletKycUserBodyNotification = createHyperWalletKycBusinessUserBodyNotification(
				HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
				HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED);

		when(kycRejectionReasonServiceMock.getReasonTypes(hyperWalletKycUserBodyNotification))
				.thenReturn(List.of(KYCRejectionReasonTypeEnum.BUSINESS_STAKEHOLDER_REQUIRED));

		final KYCUserStatusNotificationBodyModel result = testObj.convert(hyperWalletKycUserBodyNotification);

		assertThat(result.getClientUserId()).isEqualTo(CLIENT_USER_ID);
		assertThat(result.getProfileType()).isEqualTo(BUSINESS_PROFILE_TYPE);
		assertThat(result.getVerificationStatus()).isEqualTo(HyperwalletUser.VerificationStatus.NOT_REQUIRED);
		assertThat(result.getReasonsType()).containsExactly(KYCRejectionReasonTypeEnum.BUSINESS_STAKEHOLDER_REQUIRED);
	}

	@Test
	void convert_shouldTransformHyperWalletWebhookUserNotificationWithLetterOfAuthorizationRequirementToKycUserStatusNotificationModelWithLetterOfAuthorizationReasonType_whenDetailsIsNotNull() {
		final Map<String, String> hyperWalletKycUserBodyNotification = createHyperWalletKycBusinessUserBodyNotification(
				HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
				HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED);

		when(kycRejectionReasonServiceMock.getReasonTypes(hyperWalletKycUserBodyNotification))
				.thenReturn(List.of(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED));

		final KYCUserStatusNotificationBodyModel result = testObj.convert(hyperWalletKycUserBodyNotification);

		assertThat(result.getClientUserId()).isEqualTo(CLIENT_USER_ID);
		assertThat(result.getProfileType()).isEqualTo(BUSINESS_PROFILE_TYPE);
		assertThat(result.getVerificationStatus()).isEqualTo(HyperwalletUser.VerificationStatus.NOT_REQUIRED);
		assertThat(result.getReasonsType())
				.containsExactly(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED);
	}

	@Test
	void convert_shouldTransformHyperWalletWebhookNotificationToKyCUserStatusNotificationModel_whenObjectIsNotJSonObject() {
		final KYCUserStatusNotificationBodyModel result = testObj.convert(new Object());

		assertThat(result).isNull();
	}

	private Map<String, String> createHyperWalletKycUserBodyNotification(
			final HyperwalletUser.ProfileType profileType) {
		final Map<String, String> detailInfo = new HashMap<>();
		detailInfo.put("clientUserId", CLIENT_USER_ID);
		detailInfo.put("profileType", profileType.name());
		detailInfo.put("verificationStatus", REQUIRED_VERIFICATION_STATUS.name());
		if (BUSINESS_PROFILE_TYPE.equals(profileType)) {
			detailInfo.put("businessStakeholderVerificationStatus", NOT_REQUIRED_BUSINESS_VERIFICATION_STATUS.name());
			detailInfo.put("letterOfAuthorizationStatus", FAILED_LETTER_VERIFICATION_STATUS.name());
		}

		return detailInfo;
	}

	private Map<String, String> createHyperWalletKycBusinessUserBodyNotification(
			final HyperwalletUser.BusinessStakeholderVerificationStatus businessStakeholderVerificationStatus,
			HyperwalletUser.LetterOfAuthorizationStatus letterOfAuthorizationStatus) {
		final Map<String, String> detailInfo = new HashMap<>();
		detailInfo.put("clientUserId", CLIENT_USER_ID);
		detailInfo.put("profileType", BUSINESS_PROFILE_TYPE.name());
		detailInfo.put("verificationStatus", HyperwalletUser.VerificationStatus.NOT_REQUIRED.name());
		detailInfo.put("businessStakeholderVerificationStatus", businessStakeholderVerificationStatus.name());
		detailInfo.put("letterOfAuthorizationStatus", letterOfAuthorizationStatus.name());

		return detailInfo;
	}

}
