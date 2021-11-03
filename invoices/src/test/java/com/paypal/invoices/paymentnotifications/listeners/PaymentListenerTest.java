package com.paypal.invoices.paymentnotifications.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.events.PaymentEvent;
import com.paypal.invoices.paymentnotifications.service.PaymentNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentListenerTest {

	@InjectMocks
	private PaymentListener testObj;

	@Mock
	private PaymentNotificationService paymentNotificationServiceMock;

	@Mock
	private PaymentEvent paymentEventMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void sendPaymentNotification_shouldCallPaymentNotificationService() {
		when(paymentEventMock.getNotification()).thenReturn(hyperwalletWebhookNotificationMock);

		testObj.onApplicationEvent(paymentEventMock);

		verify(paymentNotificationServiceMock).processPaymentNotification(hyperwalletWebhookNotificationMock);
	}

}
