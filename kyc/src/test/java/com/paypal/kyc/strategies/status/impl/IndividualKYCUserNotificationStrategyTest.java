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
class IndividualKYCUserNotificationStrategyTest {

	@InjectMocks
	private IndividualKYCUserStatusNotificationStrategy testObj;

	private static final Long CLIENT_USER_ID = 2000L;

	@MethodSource("validVerificationStatuses")
	@ParameterizedTest
	void execute_shouldSendKycUpdateNotification_whenIndividualSeller(
			final HyperwalletUser.VerificationStatus hyperwalletVerificationStatus,
			final MiraklShopKycStatus miraklKycStatus) {
		//@formatter:off
		final KYCUserStatusNotificationBodyModel KYCUserStatusNotificationBodyModelStub = KYCUserStatusNotificationBodyModel.builder()
				.clientUserId(String
						.valueOf(CLIENT_USER_ID))
				.verificationStatus(hyperwalletVerificationStatus)
				.profileType(HyperwalletUser.ProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		final MiraklShopKycStatus result = testObj.expectedKycMiraklStatus(KYCUserStatusNotificationBodyModelStub);

		assertThat(result).isEqualTo(miraklKycStatus);
	}

	@Test
	void isApplicable_shouldReturnTrueWhenProfileTypeIsIndividual() {
		//@formatter:off
		final KYCUserStatusNotificationBodyModel KYCUserStatusNotificationBodyModelStub = KYCUserStatusNotificationBodyModel.builder()
				.profileType(HyperwalletUser.ProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		final boolean result = testObj.isApplicable(KYCUserStatusNotificationBodyModelStub);

		assertThat(result).isTrue();
	}

	@MethodSource("nonIndividualProfileTypes")
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
				Arguments.of(HyperwalletUser.VerificationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL),
				Arguments.of(HyperwalletUser.VerificationStatus.VERIFIED, MiraklShopKycStatus.APPROVED),
				Arguments.of(HyperwalletUser.VerificationStatus.REQUIRED, MiraklShopKycStatus.REFUSED),
				Arguments.of(HyperwalletUser.VerificationStatus.NOT_REQUIRED, MiraklShopKycStatus.APPROVED));
	}

	private static Stream<Arguments> nonIndividualProfileTypes() {
		return Stream.of(Arguments.of(HyperwalletUser.ProfileType.BUSINESS),
				Arguments.of(HyperwalletUser.ProfileType.UNKNOWN));
	}

}
