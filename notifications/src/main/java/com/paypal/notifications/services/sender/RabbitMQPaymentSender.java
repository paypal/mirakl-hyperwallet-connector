package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.Strategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Strategy class for payment notifications
 */
@Slf4j
@Service
@Getter
public class RabbitMQPaymentSender extends AbstractRabbitMQSender
		implements Strategy<HyperwalletWebhookNotification, Optional<Void>> {

	@Value("${notifications.payments.routingKey}.*")
	private String routingKey;

	@Value("${notifications.payments.routingKey}")
	private String notificationType;

	@Override
	protected String getRoutingKey() {
		return routingKey;
	}

	@Override
	public Optional<Void> execute(final HyperwalletWebhookNotification source) {
		send(source);
		return Optional.empty();
	}

	@Override
	public boolean isApplicable(final HyperwalletWebhookNotification source) {
		return source.getType().startsWith(getNotificationType());
	}

}
