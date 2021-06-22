package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AbstractRabbitMQSenderTest {

	private static final String ROUTING_KEY = "ROUTING_KEY";

	@Spy
	private final MyRabbitMQSender testObj = new MyRabbitMQSender();

	@Mock
	private AmqpTemplate amqpTemplateMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void send_shouldSentNotificationtoProperExchangeAndRoutingKey() {
		doReturn(ROUTING_KEY).when(testObj).getRoutingKey();
		doReturn(amqpTemplateMock).when(testObj).getAmqpTemplate();

		testObj.send(hyperwalletWebhookNotificationMock);

		verify(amqpTemplateMock).convertAndSend(testObj.getExchange(), ROUTING_KEY, hyperwalletWebhookNotificationMock);
	}

	private class MyRabbitMQSender extends AbstractRabbitMQSender {

		@Override
		protected String getRoutingKey() {
			return null;
		}

	}

}
