package com.paypal.kyc.statussynchronization.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import com.paypal.kyc.incomingnotifications.model.KYCRejectionReasonTypeEnum;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.services.converters.HyperWalletObjectToKYCUserStatusNotificationBodyModelConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperwalletUserToKycUserStatusNotificationBodyModelConverterTest {

	public static final List<KYCRejectionReasonTypeEnum> LIST_OF_REASONS = List.of(KYCRejectionReasonTypeEnum.UNKWOWN,
			KYCRejectionReasonTypeEnum.VERIFICATIONSTATUS_IND_REQUIRED,
			KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED);

	public static final String CLIENT_1_ID = "client_1";

	@InjectMocks
	private HyperwalletUserToKycUserStatusNotificationBodyModelConverter testObj;

	@Mock
	private HyperWalletObjectToKYCUserStatusNotificationBodyModelConverter hyperWalletObjectToKYCUserStatusNotificationBodyModelConverterMock;

	@Captor
	ArgumentCaptor<Map<String, Object>> hyperwalletUserMapCaptor;

	@Mock
	private KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModelMock;

	@Test
	void hyperwalletUser_shouldCreateKycUserStatusNotificationBodyModel() {

		final HyperwalletUser hyperwalletUser = new HyperwalletUser();
		hyperwalletUser.setVerificationStatus(HyperwalletUser.VerificationStatus.VERIFIED);
		hyperwalletUser.setBusinessStakeholderVerificationStatus(
				HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED);
		hyperwalletUser.setClientUserId(CLIENT_1_ID);
		hyperwalletUser.setProfileType(HyperwalletUser.ProfileType.INDIVIDUAL);
		hyperwalletUser.setLetterOfAuthorizationStatus(HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED);
		final HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setCategory(KYCConstants.HwDocuments.PROOF_OF_ADDRESS);
		hyperwalletUser.setDocuments(List.of(hyperwalletVerificationDocument));
		when(hyperWalletObjectToKYCUserStatusNotificationBodyModelConverterMock.convert(any()))
				.thenReturn(kycUserStatusNotificationBodyModelMock);

		final KYCUserStatusNotificationBodyModel result = testObj.convert(hyperwalletUser);
		assertThat(result).isEqualTo(kycUserStatusNotificationBodyModelMock);

		verify(hyperWalletObjectToKYCUserStatusNotificationBodyModelConverterMock)
				.convert(hyperwalletUserMapCaptor.capture());
		final Map<String, Object> hyperwalletUserMapCaptorValue = hyperwalletUserMapCaptor.getValue();
		assertThat(hyperwalletUserMapCaptorValue).containsEntry("verificationStatus", "VERIFIED")
				.containsEntry("businessStakeholderVerificationStatus", "VERIFIED")
				.containsEntry("clientUserId", CLIENT_1_ID).containsEntry("profileType", "INDIVIDUAL")
				.containsEntry("letterOfAuthorizationStatus", "VERIFIED");
		final List<Map<String, Object>> documents = (List<Map<String, Object>>) hyperwalletUserMapCaptorValue
				.get("documents");
		assertThat(documents.get(0)).containsEntry("category", KYCConstants.HwDocuments.PROOF_OF_ADDRESS);
	}

}
