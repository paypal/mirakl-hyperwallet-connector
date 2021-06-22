package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQKycUserSenderTest {

	private static final String ROUTING_KEY = "ROUTING_KEY";

	@Spy
	@InjectMocks
	private RabbitMQKycUserSender testObj;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void getRoutingKey_shouldReturnRoutingKeyAttribute() {
		doReturn(ROUTING_KEY).when(testObj).getRoutingKey();
		final var result = testObj.getRoutingKey();

		assertThat(result).isEqualTo(ROUTING_KEY);
	}

	@Test
	void execute_shouldReturnEmptyOptional() {
		doNothing().when(testObj).send(hyperwalletWebhookNotificationMock);

		final var result = testObj.execute(hyperwalletWebhookNotificationMock);

		verify(testObj).send(hyperwalletWebhookNotificationMock);
		assertThat(result).isNotPresent();
	}

}
