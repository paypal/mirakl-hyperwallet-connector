package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.Strategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Strategy class for user notifications
 */
@Slf4j
@Service
public class RabbitMQKycUserSender extends AbstractRabbitMQSender
		implements Strategy<HyperwalletWebhookNotification, Optional<Void>> {

	@Value("${notifications.users.kyc.routingKey}.*")
	private String kycRoutingKey;

	@Value("${notifications.users.kyc.routingKey}")
	private String notificationType;

	@Override
	protected String getRoutingKey() {
		return kycRoutingKey;
	}

	@Override
	public Optional<Void> execute(final HyperwalletWebhookNotification notification) {
		send(notification);
		return Optional.empty();
	}

	@Override
	public boolean isApplicable(final HyperwalletWebhookNotification source) {
		return source.getType().startsWith(notificationType);
	}

}
