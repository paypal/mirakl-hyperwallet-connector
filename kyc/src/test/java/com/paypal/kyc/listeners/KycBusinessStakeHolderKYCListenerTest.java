package com.paypal.kyc.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.events.KycBusinessStakeholderEvent;
import com.paypal.kyc.service.KYCBusinessStakeholderNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KycBusinessStakeHolderKYCListenerTest {

	@InjectMocks
	private KycBusinessStakeHolderKYCListener testObj;

	@Mock
	private KYCBusinessStakeholderNotificationService kycBusinessStakeholderNotificationServiceMock;

	@Mock
	private KycBusinessStakeholderEvent kycBusinessStakeholderEventMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void sendKycNotification_shouldCallKycBusinessStakeholderNotificationService() {
		when(kycBusinessStakeholderEventMock.getNotification()).thenReturn(hyperwalletWebhookNotificationMock);

		testObj.onApplicationEvent(kycBusinessStakeholderEventMock);

		verify(kycBusinessStakeholderNotificationServiceMock)
				.updateBusinessStakeholderKYCStatus(hyperwalletWebhookNotificationMock);
	}

}
