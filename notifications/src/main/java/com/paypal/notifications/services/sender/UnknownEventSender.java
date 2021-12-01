package com.paypal.notifications.services.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.Strategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Strategy class for unknown notifications Receives notifications, but does not process
 * them
 */
@Slf4j
@Service
public class UnknownEventSender implements Strategy<HyperwalletWebhookNotification, Void> {

	private final Set<Strategy<HyperwalletWebhookNotification, Void>> allRegisteredNotifications;

	public UnknownEventSender(final Set<Strategy<HyperwalletWebhookNotification, Void>> allRegisteredNotifications) {
		this.allRegisteredNotifications = allRegisteredNotifications;
	}

	@Override
	public Void execute(final HyperwalletWebhookNotification notification) {
		log.info("Skip processing notification [{}]", notification.getType());

		return null;
	}

	@Override
	public boolean isApplicable(final HyperwalletWebhookNotification source) {
		return allRegisteredNotifications.stream()
				.noneMatch(notificationStrategy -> notificationStrategy.isApplicable(source));
	}

}
