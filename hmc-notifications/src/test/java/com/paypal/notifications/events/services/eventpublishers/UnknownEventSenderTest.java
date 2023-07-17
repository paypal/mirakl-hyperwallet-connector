package com.paypal.notifications.events.services.eventpublishers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.notifications.events.services.eventpublishers.UnknownEventSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnknownEventSenderTest {

	@InjectMocks
	private UnknownEventSender testObj;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Mock
	private Strategy<HyperwalletWebhookNotification, Void> strategy1Mock, strategy2Mock;

	@BeforeEach
	void setUp() {
		testObj = new UnknownEventSender(Set.of(strategy1Mock, strategy2Mock));
	}

	@Test
	void execute_shouldReturnNull() {
		final Void result = testObj.execute(hyperwalletWebhookNotificationMock);

		assertThat(result).isNull();
	}

	@Test
	void isApplicable_whenNoStrategiesAreAvailable_shouldReturnTrue() {
		testObj = new UnknownEventSender(Collections.emptySet());

		final boolean result = testObj.isApplicable(hyperwalletWebhookNotificationMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_whenNoStrategyIsApplicable_shouldReturnTrue() {
		final boolean result = testObj.isApplicable(hyperwalletWebhookNotificationMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_whenSomeStrategyIsApplicable_shouldReturnFalse() {
		when(strategy2Mock.isApplicable(hyperwalletWebhookNotificationMock)).thenReturn(true);

		final boolean result = testObj.isApplicable(hyperwalletWebhookNotificationMock);

		assertThat(result).isFalse();
	}

}
