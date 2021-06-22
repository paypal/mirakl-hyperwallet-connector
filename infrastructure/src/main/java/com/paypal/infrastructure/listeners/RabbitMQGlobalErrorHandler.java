package com.paypal.infrastructure.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Global error handler for rabbitMQ notifications to avoid infinite retries
 */
@Slf4j
@Service
public class RabbitMQGlobalErrorHandler implements RabbitListenerErrorHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object handleError(final Message amqpMessage, final org.springframework.messaging.Message<?> message,
			final ListenerExecutionFailedException exception) {
		log.error(Arrays.toString(amqpMessage.getBody()));
		log.error(message.getPayload().toString());
		throw new AmqpRejectAndDontRequeueException("Something failed processing the notifications, avoiding retry",
				exception);
	}

}
