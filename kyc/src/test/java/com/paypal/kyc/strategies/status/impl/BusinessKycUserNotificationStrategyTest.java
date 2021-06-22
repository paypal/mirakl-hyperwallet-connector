package com.paypal.kyc.strategies.status.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.domain.shop.MiraklShopKycStatus;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BusinessKycUserNotificationStrategyTest {

	private static final Long CLIENT_USER_ID = 2000L;

	@InjectMocks
	private BusinessKYCUserStatusNotificationStrategy testObj;

	@MethodSource("validVerificationStatuses")
	@ParameterizedTest
	void expectedKycMiraklStatus_shouldSetCorrectMiraklStatusBasedOnHyperwalletUserStatusesForBusiness(
			final HyperwalletUser.VerificationStatus verificationStatus,
			final HyperwalletUser.BusinessStakeholderVerificationStatus businessStakeholderVerificationStatus,
			final HyperwalletUser.LetterOfAuthorizationStatus letterOfAuthorizationStatus,
			final MiraklShopKycStatus miraklShopKycStatus) {
		//@formatter:off
        final KYCUserStatusNotificationBodyModel KYCUserStatusNotificationBodyModelStub = KYCUserStatusNotificationBodyModel.builder()
                .clientUserId(String
                        .valueOf(CLIENT_USER_ID))
                .verificationStatus(verificationStatus)
                .businessStakeholderVerificationStatus(businessStakeholderVerificationStatus)
                .letterOfAuthorizationStatus(letterOfAuthorizationStatus)
                .profileType(HyperwalletUser.ProfileType.BUSINESS)
                .build();
        //@formatter:on

		final var result = testObj.expectedKycMiraklStatus(KYCUserStatusNotificationBodyModelStub);

		assertThat(result).isEqualTo(miraklShopKycStatus);

	}

	@Test
	void expectedKycMiraklStatus_shouldSetCorrectMiraklStatus_whenVerificationStatusIsNull() {
		//@formatter:off
		final KYCUserStatusNotificationBodyModel KYCUserStatusNotificationBodyModelStub = KYCUserStatusNotificationBodyModel.builder()
				.clientUserId(String
						.valueOf(CLIENT_USER_ID))
				.verificationStatus(null)
				.businessStakeholderVerificationStatus(HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED)
				.letterOfAuthorizationStatus(HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED)
				.profileType(HyperwalletUser.ProfileType.BUSINESS)
				.build();
		//@formatter:on

		final var result = testObj.expectedKycMiraklStatus(KYCUserStatusNotificationBodyModelStub);

		assertThat(result).isEqualTo(MiraklShopKycStatus.APPROVED);

	}

	@Test
	void expectedKycMiraklStatus_shouldSetCorrectMiraklStatus_whenBusinessStakeHolderVerificationStatusIsNull() {
		//@formatter:off
		final KYCUserStatusNotificationBodyModel KYCUserStatusNotificationBodyModelStub = KYCUserStatusNotificationBodyModel.builder()
				.clientUserId(String
						.valueOf(CLIENT_USER_ID))
				.verificationStatus(HyperwalletUser.VerificationStatus.VERIFIED)
				.businessStakeholderVerificationStatus(null)
				.letterOfAuthorizationStatus(HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED)
				.profileType(HyperwalletUser.ProfileType.BUSINESS)
				.build();
		//@formatter:on

		final var result = testObj.expectedKycMiraklStatus(KYCUserStatusNotificationBodyModelStub);

		assertThat(result).isEqualTo(MiraklShopKycStatus.APPROVED);

	}

	@Test
	void expectedKycMiraklStatus_shouldSetCorrectMiraklStatus_whenLetterOfAuthorizationStatusIsNull() {
		//@formatter:off
		final KYCUserStatusNotificationBodyModel KYCUserStatusNotificationBodyModelStub = KYCUserStatusNotificationBodyModel.builder()
				.clientUserId(String
						.valueOf(CLIENT_USER_ID))
				.verificationStatus(HyperwalletUser.VerificationStatus.VERIFIED)
				.businessStakeholderVerificationStatus(HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED)
				.letterOfAuthorizationStatus(null)
				.profileType(HyperwalletUser.ProfileType.BUSINESS)
				.build();
		//@formatter:on

		final var result = testObj.expectedKycMiraklStatus(KYCUserStatusNotificationBodyModelStub);

		assertThat(result).isEqualTo(MiraklShopKycStatus.APPROVED);

	}

	@Test
	void isApplicable_shouldReturnTrueWhenProfileTypeIsBusiness() {

		//@formatter:off
        final KYCUserStatusNotificationBodyModel KYCUserStatusNotificationBodyModelStub = KYCUserStatusNotificationBodyModel.builder()
                .profileType(HyperwalletUser.ProfileType.BUSINESS)
                .build();
        //@formatter:on

		final boolean result = testObj.isApplicable(KYCUserStatusNotificationBodyModelStub);

		assertThat(result).isTrue();
	}

	@MethodSource("nonBusinessProfileTypes")
	@ParameterizedTest
	void isApplicable_shouldReturnFalseWhenProfileTypeIsNotIndividual(final HyperwalletUser.ProfileType profileType) {

		//@formatter:off
        final KYCUserStatusNotificationBodyModel KYCUserStatusNotificationBodyModelStub = KYCUserStatusNotificationBodyModel.builder()
                .profileType(profileType)
                .build();
        //@formatter:on

		final boolean result = testObj.isApplicable(KYCUserStatusNotificationBodyModelStub);

		assertThat(result).isFalse();
	}

	private static Stream<Arguments> validVerificationStatuses() {
		return Stream.of(
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.APPROVED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.APPROVED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.APPROVED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.APPROVED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.APPROVED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.APPROVED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.APPROVED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.APPROVED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.NOT_REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.UNDER_REVIEW,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED,
						HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED,
						HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, MiraklShopKycStatus.REFUSED));
	}

	private static Stream<Arguments> nonBusinessProfileTypes() {
		return Stream.of(Arguments.of(HyperwalletUser.ProfileType.INDIVIDUAL),
				Arguments.of(HyperwalletUser.ProfileType.UNKNOWN));
	}

}
