package com.paypal.kyc.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.events.KycUserEvent;
import com.paypal.kyc.service.KYCUserNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KycUserEventListenerTest {

	@InjectMocks
	private KycUserEventListener testObj;

	@Mock
	private KYCUserNotificationService KYCNotificationServiceMock;

	@Mock
	private KycUserEvent kycUserEventMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void sendKycNotification_shouldCallKyCUserNotificationService() {
		when(kycUserEventMock.getNotification()).thenReturn(hyperwalletWebhookNotificationMock);

		testObj.onApplicationEvent(kycUserEventMock);

		verify(KYCNotificationServiceMock).updateUserKYCStatus(hyperwalletWebhookNotificationMock);
		verify(KYCNotificationServiceMock).updateUserDocumentsFlags(hyperwalletWebhookNotificationMock);
	}

}
