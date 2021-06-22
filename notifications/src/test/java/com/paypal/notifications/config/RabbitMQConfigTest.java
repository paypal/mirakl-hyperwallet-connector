package com.paypal.notifications.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RabbitMQConfigTest {

	private static final String PAYMENT_QUEUE_NAME = "PAYMENT_QUEUE";

	private static final String USER_KYC_QUEUE_NAME = "USER_KYC_QUEUE";

	private static final String EXCHANGE = "EXCHANGE";

	private static final String PAYMENT_ROUTING_KEY = "PAYMENT_ROUTING_KEY";

	private static final String USER_KYC_ROUTING_KEY = "USER_KYC_ROUTING_KEY";

	@InjectMocks
	private RabbitMQConfig testObj;

	@BeforeEach
	void setUp() {
		testObj.setExchange(EXCHANGE);
		testObj.setPaymentQueueName(PAYMENT_QUEUE_NAME);
		testObj.setUsersKycQueueName(USER_KYC_QUEUE_NAME);
		testObj.setPaymentRoutingKey(PAYMENT_ROUTING_KEY);
		testObj.setUsersKycRoutingKey(USER_KYC_ROUTING_KEY);
	}

	@Test
	void paymentQueue_shouldReturnPaymentQueueName() {
		final var result = testObj.paymentQueue();

		assertThat(result.getName()).isEqualTo(PAYMENT_QUEUE_NAME);
	}

	@Test
	void usersQueue_shouldReturnUserKycQueueName() {
		final var result = testObj.usersQueue();

		assertThat(result.getName()).isEqualTo(USER_KYC_QUEUE_NAME);
	}

	@Test
	void topicExchange_shouldReturnTopicTypeExchange() {
		final var result = testObj.topicExchange();
		assertThat(result.getName()).isEqualTo(EXCHANGE);
		assertThat(result.getType()).isEqualTo("topic");
	}

	@Test
	void paymentBinding_shouldReturnBindingBetweenExchangeAndPaymentQueue() {
		final var result = testObj.paymentBinding(testObj.topicExchange());

		assertThat(result.getExchange()).isEqualTo(EXCHANGE);
		assertThat(result.getDestination()).isEqualTo(PAYMENT_QUEUE_NAME);
		assertThat(result.getRoutingKey()).isEqualTo(PAYMENT_ROUTING_KEY);
	}

	@Test
	void userBinding_shouldReturnBindingBetweenExchangeAndUserKycQueue() {
		final var result = testObj.userBinding(testObj.topicExchange());

		assertThat(result.getExchange()).isEqualTo(EXCHANGE);
		assertThat(result.getDestination()).isEqualTo(USER_KYC_QUEUE_NAME);
		assertThat(result.getRoutingKey()).isEqualTo(USER_KYC_ROUTING_KEY);
	}

	@Test
	void jsonMessageConverter_shouldReturnMessageConverterInstance() {
		final var result = testObj.jsonMessageConverter();

		assertThat(result).isInstanceOf(MessageConverter.class);

	}

}
