package com.paypal.infrastructure.listeners;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitMQGlobalErrorHandlerTest {

	@InjectMocks
	private RabbitMQGlobalErrorHandler testObj;

	@Mock
	private Message amqpMessageMock;

	@Mock
	private org.springframework.messaging.Message<String> messageMock;

	@Mock
	private ListenerExecutionFailedException exceptionMock;

	private byte[] byteValueStub;

	@Test
	void handleError_shouldThrowAmqpRejectAndDontRequeueException() {
		when(messageMock.getPayload()).thenReturn("Message");
		when(amqpMessageMock.getBody()).thenReturn(byteValueStub);
		assertThatThrownBy(() -> testObj.handleError(amqpMessageMock, messageMock, exceptionMock))
				.isInstanceOf(AmqpRejectAndDontRequeueException.class)
				.hasMessage("Something failed processing the notifications, avoiding retry");
	}

}
