package com.paypal.invoices.paymentnotifications.consumer;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.invoices.paymentnotifications.service.PaymentNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMQPaymentConsumerTest {

	@InjectMocks
	private RabbitMQPaymentConsumer testObj;

	@Mock
	private PaymentNotificationService paymentNotificationServiceMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void sendPaymentNotification_shouldCallPaymentNotificationService() {

		testObj.sendPaymentNotification(hyperwalletWebhookNotificationMock);

		verify(paymentNotificationServiceMock).processPaymentNotification(hyperwalletWebhookNotificationMock);
	}

}
