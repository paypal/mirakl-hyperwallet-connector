package com.paypal.kyc.strategies.documents.flags.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KYCUserDocumentFlagIndividualStrategyTest {

	@Spy
	@InjectMocks
	private KYCUserDocumentFlagIndividualStrategy testObj;

	@Mock
	private KYCUserDocumentFlagsNotificationBodyModel notificationMock;

	@Test
	void isApplicable_shouldReturnTrueWhenUserReceivedIsIndividualAndVerificationStatusIsRequired() {

		//@formatter:off
        KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
                .builder()
                .profileType(HyperwalletUser.ProfileType.INDIVIDUAL)
                .verificationStatus(HyperwalletUser.VerificationStatus.REQUIRED)
                .build();
        //@formatter:on
		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenUserReceivedIndividualButVerificationStatusIsNotRequired() {
		//@formatter:off
        KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
                .builder()
                .profileType(HyperwalletUser.ProfileType.INDIVIDUAL)
                .verificationStatus(HyperwalletUser.VerificationStatus.FAILED)
                .build();
        //@formatter:on
		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenUserReceivedIsProfessionalAndVerificationStatusIsRequired() {

		//@formatter:off
		KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder()
				.profileType(HyperwalletUser.ProfileType.BUSINESS)
				.verificationStatus(HyperwalletUser.VerificationStatus.REQUIRED)
				.build();
		//@formatter:on
		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isFalse();
	}

	@Test
	void execute_verifyCallsParentFillMiraklProofIdentityOrBusinessFlagStatus() {
		doReturn(Optional.empty()).when(testObj).superFillMiraklProofIdentityOrBusinessFlagStatus(notificationMock);

		testObj.execute(notificationMock);

		verify(testObj).superFillMiraklProofIdentityOrBusinessFlagStatus(notificationMock);
	}

}
