package com.paypal.kyc.incomingnotifications.handlers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.incomingnotifications.services.KYCUserNotificationService;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KycUserNotificationHandlerTest {

	@InjectMocks
	private KycUserNotificationHandler testObj;

	@Mock
	private KYCUserNotificationService kycUserNotificationServiceMock;

	@Mock
	private NotificationEntity notificationEntityMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void supports_whenTypeIsUSR_shouldReturnTrue() {
		assertThat(testObj.supports(NotificationType.USR)).isTrue();
	}

	@ParameterizedTest
	@EnumSource(value = NotificationType.class, names = { "PMT", "STK", "TRM", "UNK" })
	void supports_whenTypeIsNotUSR_shouldReturnFalse(final NotificationType notificationType) {
		assertThat(testObj.supports(notificationType)).isFalse();
	}

	@Test
	void process_shouldDelegateToKycUserNotificationService() {
		when(notificationEntityMock.getWebHookToken()).thenReturn("wbh-token-1");

		testObj.process(notificationEntityMock, hyperwalletWebhookNotificationMock);

		verify(kycUserNotificationServiceMock).updateUserKYCStatus(hyperwalletWebhookNotificationMock);
		verify(kycUserNotificationServiceMock).updateUserDocumentsFlags(hyperwalletWebhookNotificationMock);
	}

}
