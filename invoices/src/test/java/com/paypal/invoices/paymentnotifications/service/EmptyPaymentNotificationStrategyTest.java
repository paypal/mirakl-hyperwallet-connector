package com.paypal.invoices.paymentnotifications.service;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmptyPaymentNotificationStrategyTest {

	LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.WARN)
			.recordForType(EmptyPaymentNotificationStrategy.class);

	@InjectMocks
	private EmptyPaymentNotificationStrategy testObj;

	@Mock
	private PaymentNotificationBodyModel paymentNotificationBodyModelMock;

	@Test
	void execute() {
		logTrackerStub.recordForLevel(LogTracker.LogLevel.WARN);

		testObj.execute(null);

		assertThat(logTrackerStub.contains("Payment notification received with a null object.")).isTrue();
	}

	@Test
	void isApplicable_shouldReturnTrue_whenPaymentNotificationBodyModelIsNull() {

		final boolean result = testObj.isApplicable(null);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelIsNotNull() {

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isFalse();
	}

}
