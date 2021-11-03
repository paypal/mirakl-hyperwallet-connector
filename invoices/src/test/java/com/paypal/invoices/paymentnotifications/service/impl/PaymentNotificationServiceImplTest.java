package com.paypal.invoices.paymentnotifications.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import com.paypal.invoices.paymentnotifications.service.PaymentNotificationExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationServiceImplTest {

	@InjectMocks
	private PaymentNotificationServiceImpl testObj;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Mock
	private PaymentNotificationBodyModel paymentNotificationBodyModelMock;

	@Mock
	private Converter<Object, PaymentNotificationBodyModel> paymentNotificationBodyModelConverterMock;

	@Mock
	private PaymentNotificationExecutor paymentNotificationExecutorMock;

	@Test
	void processPaymentNotification_shouldNotSendAnyEmailWhenPaymentNotificationIsNull() {
		when(paymentNotificationBodyModelConverterMock.convert(hyperwalletWebhookNotificationMock.getObject()))
				.thenReturn(paymentNotificationBodyModelMock);

		testObj.processPaymentNotification(hyperwalletWebhookNotificationMock);

		verify(paymentNotificationExecutorMock).execute(paymentNotificationBodyModelMock);
	}

}
