package com.paypal.notifications.events.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.notifications.events.services.EventSenderExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EventSenderExecutorTest {

	private EventSenderExecutor testObj;

	@Mock
	private Strategy<HyperwalletWebhookNotification, Void> strategy1, strategy2;

	@BeforeEach
	void setUp() {
		testObj = new EventSenderExecutor(Set.of(strategy1, strategy2));
	}

	@Test
	void getStrategies_shouldReturnSetOfAvailableStrategies() {
		final Set<Strategy<HyperwalletWebhookNotification, Void>> result = testObj.getStrategies();

		assertThat(result).containsExactlyInAnyOrder(strategy1, strategy2);
	}

}
