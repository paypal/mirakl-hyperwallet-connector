package com.paypal.notifications.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class to handle the Rabbit MQ
 */
@Data
@Configuration
@PropertySource({ "classpath:notifications.properties" })
public class RabbitMQConfig {

	@Value("${notifications.payments.queue}")
	String paymentQueueName;

	@Value("${notifications.users.kyc.queue}")
	String usersKycQueueName;

	@Value("${notifications.business.stakeholders.kyc.queue}")
	String businessStakeholdersKycQueueName;

	@Value("${notifications.exchange}")
	String exchange;

	@Value("${notifications.payments.routingKey}.*")
	private String paymentRoutingKey;

	@Value("${notifications.users.kyc.routingKey}.*")
	private String usersKycRoutingKey;

	@Value("${notifications.business.stakeholders.kyc.routingKey}.*")
	private String businessStakeHoldersKycRoutingKey;

	@Bean
	Queue paymentQueue() {
		return new Queue(paymentQueueName, false);
	}

	@Bean
	Queue usersQueue() {
		return new Queue(usersKycQueueName, false);
	}

	@Bean
	Queue businessStakeHoldersQueue() {
		return new Queue(businessStakeholdersKycQueueName, false);
	}

	@Bean
	TopicExchange topicExchange() {
		return new TopicExchange(exchange);
	}

	@Bean
	Binding paymentBinding(final TopicExchange exchange) {
		return BindingBuilder.bind(paymentQueue()).to(exchange).with(paymentRoutingKey);
	}

	@Bean
	Binding userBinding(final TopicExchange exchange) {
		return BindingBuilder.bind(usersQueue()).to(exchange).with(usersKycRoutingKey);
	}

	@Bean
	Binding businessStakeholderBinding(final TopicExchange exchange) {
		return BindingBuilder.bind(businessStakeHoldersQueue()).to(exchange).with(businessStakeHoldersKycRoutingKey);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

}
