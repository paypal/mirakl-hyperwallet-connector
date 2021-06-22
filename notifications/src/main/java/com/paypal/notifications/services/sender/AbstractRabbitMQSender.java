package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

/**
 * Configuration for Rabbit MQ exchange
 */
@Getter
@Slf4j
public abstract class AbstractRabbitMQSender {

	@Resource
	private AmqpTemplate amqpTemplate;

	@Value("${notifications.exchange}")
	private String exchange;

	protected void send(final HyperwalletWebhookNotification notification) {
		log.info("Notification sent to [{}]", notification.getType());
		getAmqpTemplate().convertAndSend(getExchange(), getRoutingKey(), notification);

	}

	protected abstract String getRoutingKey();

}
