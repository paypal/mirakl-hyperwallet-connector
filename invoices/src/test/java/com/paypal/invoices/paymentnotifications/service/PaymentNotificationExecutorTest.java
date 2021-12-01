package com.paypal.invoices.paymentnotifications.service;

import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationExecutorTest {

	@InjectMocks
	private PaymentNotificationExecutor testObj;

	@Mock
	private Strategy<PaymentNotificationBodyModel, Void> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new PaymentNotificationExecutor(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<PaymentNotificationBodyModel, Void>> result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
