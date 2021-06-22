package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.Strategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Strategy class for unknown notifications
 */
@Slf4j
@Service
public class RabbitMQOtherSender implements Strategy<HyperwalletWebhookNotification, Optional<Void>> {

	private final Set<Strategy<HyperwalletWebhookNotification, Optional<Void>>> allRegisteredNotifications;

	public RabbitMQOtherSender(
			final Set<Strategy<HyperwalletWebhookNotification, Optional<Void>>> allRegisteredNotifications) {
		this.allRegisteredNotifications = allRegisteredNotifications;
	}

	@Override
	public Optional<Void> execute(final HyperwalletWebhookNotification notification) {
		log.info("Skip processing notification [{}]", notification.getType());

		return Optional.empty();
	}

	@Override
	public boolean isApplicable(final HyperwalletWebhookNotification source) {
		return allRegisteredNotifications.stream().filter(notificationStrategy -> !notificationStrategy.equals(this))
				.noneMatch(notificationStrategy -> notificationStrategy.isApplicable(source));
	}

}
