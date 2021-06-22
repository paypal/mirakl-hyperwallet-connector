package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RabbitMQOtherSenderTest {

	@InjectMocks
	private RabbitMQOtherSender testObj;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	private Set<Strategy<HyperwalletWebhookNotification, Optional<Void>>> allRegisteredNotificationsStub;

	@Mock
	private Strategy<HyperwalletWebhookNotification, Optional<Void>> strategyMock;

	@BeforeEach
	void setUp() {
		allRegisteredNotificationsStub = Set.of(strategyMock);
		testObj = new RabbitMQOtherSender(allRegisteredNotificationsStub);
	}

	@Test
	void execute_shouldReturnEmptyOptional() {
		final var result = testObj.execute(hyperwalletWebhookNotificationMock);

		assertThat(result).isEmpty();
	}

	@Test
	void isApplicable_shouldReturnTrue_whenNoStrategyIsApplicable() {
		Mockito.when(strategyMock.isApplicable(hyperwalletWebhookNotificationMock)).thenReturn(Boolean.FALSE);

		final var result = testObj.isApplicable(hyperwalletWebhookNotificationMock);

		assertThat(result).isTrue();
	}

}
