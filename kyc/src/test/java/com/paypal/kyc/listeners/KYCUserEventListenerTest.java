package com.paypal.kyc.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.service.KYCUserNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KYCUserEventListenerTest {

	private static final String NOTIFICATION_TYPE = "KYC";

	private KYCUserEventListener testObj;

	@Mock
	private KYCUserNotificationService KYCNotificationServiceMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCUserEventListener(null, KYCNotificationServiceMock, null, null);
	}

	@Test
	void processNotification_shouldCallUpdateForKYCStatusAndDocumentFlags() {
		testObj.processNotification(hyperwalletWebhookNotificationMock);

		verify(KYCNotificationServiceMock).updateUserKYCStatus(hyperwalletWebhookNotificationMock);
		verify(KYCNotificationServiceMock).updateUserDocumentsFlags(hyperwalletWebhookNotificationMock);
	}

	@Test
	void getNotificationType_shouldReturnKYC() {
		final String result = testObj.getNotificationType();

		assertThat(result).isEqualTo(NOTIFICATION_TYPE);
	}

}
