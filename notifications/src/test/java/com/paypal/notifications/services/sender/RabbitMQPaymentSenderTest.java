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
class RabbitMQPaymentSenderTest {

	private static final String PAYMENTS = "PAYMENTS";

	@Spy
	@InjectMocks
	private RabbitMQPaymentSender testObj;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void execute_shouldReturnEmptyOptional() {
		doNothing().when(testObj).send(hyperwalletWebhookNotificationMock);

		final var result = testObj.execute(hyperwalletWebhookNotificationMock);

		verify(testObj).send(hyperwalletWebhookNotificationMock);
		assertThat(result).isNotPresent();
	}

	@Test
	void isApplicable_shouldReturnTrue_whenNoStrategyIsApplicable() {
		when(hyperwalletWebhookNotificationMock.getType()).thenReturn(PAYMENTS);
		doReturn(PAYMENTS).when(testObj).getNotificationType();

		final var result = testObj.isApplicable(hyperwalletWebhookNotificationMock);

		assertThat(result).isTrue();
	}

}
