package com.paypal.kyc.strategies.documents.flags.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KYCUserDocumentFlagProofOfBusinessStrategyTest {

	@Spy
	@InjectMocks
	private KYCUserDocumentFlagProofOfBusinessStrategy testObj;

	@Mock
	private KYCUserDocumentFlagsNotificationBodyModel notificationMock;

	@Test
	void isApplicable_shouldReturnTrueWhenUserReceivedIsBusinessAndLetterOfAuthorizationStatusIsRequired() {
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder().profileType(HyperwalletUser.ProfileType.BUSINESS)
				.verificationStatus(HyperwalletUser.VerificationStatus.REQUIRED).build();

		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenUserReceivedIsNotBusiness() {
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder().profileType(HyperwalletUser.ProfileType.INDIVIDUAL)
				.verificationStatus(HyperwalletUser.VerificationStatus.REQUIRED).build();

		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenUserReceivedIsBusinessButItsLetterAuthorizationStatusIsNotRequired() {
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder().profileType(HyperwalletUser.ProfileType.BUSINESS)
				.verificationStatus(HyperwalletUser.VerificationStatus.VERIFIED).build();

		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isFalse();
	}

	@Test
	void execute_verifyCallsParentFillMiraklProofIdentityOrBusinessFlagStatus() {
		doNothing().when(testObj).superFillMiraklProofIdentityOrBusinessFlagStatus(notificationMock);

		testObj.execute(notificationMock);

		verify(testObj).superFillMiraklProofIdentityOrBusinessFlagStatus(notificationMock);
	}

}
