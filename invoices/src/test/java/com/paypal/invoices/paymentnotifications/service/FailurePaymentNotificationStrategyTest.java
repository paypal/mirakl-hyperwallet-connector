package com.paypal.invoices.paymentnotifications.service;

import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.invoices.infraestructure.configuration.PaymentNotificationConfig;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailurePaymentNotificationStrategyTest {

	private static final String FAILED = "FAILED";

	private static final String NOT_FAILED = "NOT_FAILED";

	@InjectMocks
	private FailurePaymentNotificationStrategy testObj;

	@Mock
	private PaymentNotificationBodyModel paymentNotificationBodyModelMock;

	@Mock
	private PaymentNotificationConfig paymentNotificationConfigMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Test
	void executeProcessPaymentNotification_shouldSendEmailNotificationWhenPaymentNotificationIsInAFailureStatus() {
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn("FAILED");
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn("ClientPaymentID");

		testObj.execute(paymentNotificationBodyModelMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Payment Issue - ClientPaymentID",
				"There was an issue with payment of ClientPaymentID invoice. The payment status is FAILED. Please login to Hyperwallet to view and resolve the payment issue.");
	}

	@Test
	void isApplicable_shouldReturnTrue_whenPaymentNotificationBodyModelHasFailed() {
		when(paymentNotificationConfigMock.getFailureStatuses()).thenReturn(List.of(FAILED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(FAILED);

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelHasNotFailed() {
		when(paymentNotificationConfigMock.getFailureStatuses()).thenReturn(List.of(FAILED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(NOT_FAILED);

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelIsNull() {
		final boolean result = testObj.isApplicable(null);

		assertThat(result).isFalse();
	}

}
