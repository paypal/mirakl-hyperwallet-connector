package com.paypal.invoices.paymentnotifications.handlers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.invoices.paymentnotifications.services.PaymentNotificationService;
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
class PaymentNotificationHandlerTest {

	@InjectMocks
	private PaymentNotificationHandler testObj;

	@Mock
	private PaymentNotificationService paymentNotificationServiceMock;

	@Mock
	private NotificationEntity notificationEntityMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void supports_whenTypeIsPMT_shouldReturnTrue() {
		assertThat(testObj.supports(NotificationType.PMT)).isTrue();
	}

	@ParameterizedTest
	@EnumSource(value = NotificationType.class, names = { "USR", "STK", "TRM", "UNK" })
	void supports_whenTypeIsNotPMT_shouldReturnFalse(final NotificationType notificationType) {
		assertThat(testObj.supports(notificationType)).isFalse();
	}

	@Test
	void process_shouldDelegateToPaymentNotificationService() {
		when(notificationEntityMock.getWebHookToken()).thenReturn("wbh-token-1");

		testObj.process(notificationEntityMock, hyperwalletWebhookNotificationMock);

		verify(paymentNotificationServiceMock).processPaymentNotification(hyperwalletWebhookNotificationMock);
	}

}
