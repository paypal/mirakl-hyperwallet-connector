package com.paypal.invoices.paymentnotifications.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.invoices.paymentnotifications.services.PaymentNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentListenerTest {

	private static final String NOTIFICATION_TYPE = "payment";

	private PaymentListener testObj;

	@Mock
	private PaymentNotificationService paymentNotificationServiceMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@BeforeEach
	void setUp() {
		testObj = new PaymentListener(null, paymentNotificationServiceMock, null, null);
	}

	@Test
	void processNotification_shouldCallPaymentNotificationService() {
		testObj.processNotification(hyperwalletWebhookNotificationMock);

		verify(paymentNotificationServiceMock).processPaymentNotification(hyperwalletWebhookNotificationMock);
	}

	@Test
	void getNotificationType_shouldReturnPayment() {
		final String result = testObj.getNotificationType();

		assertThat(result).isEqualTo(NOTIFICATION_TYPE);
	}

}
