package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RabbitMQSenderFactoryTest {

	@InjectMocks
	private RabbitMQSenderFactorySingle testObj;

	@Mock
	private Strategy<HyperwalletWebhookNotification, Optional<Void>> strategy1, strategy2;

	@BeforeEach
	void setUp() {
		testObj = new RabbitMQSenderFactorySingle(Set.of(strategy1, strategy2));
	}

	@Test
	void getStrategies_shouldReturnSetOfAvailableStrategies() {

		final var result = testObj.getStrategies();

		assertThat(result).containsExactlyInAnyOrder(strategy1, strategy2);

	}

}
