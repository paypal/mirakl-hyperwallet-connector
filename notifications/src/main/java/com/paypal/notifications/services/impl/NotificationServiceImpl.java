package com.paypal.notifications.services.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.StrategyFactory;
import com.paypal.notifications.services.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final StrategyFactory<HyperwalletWebhookNotification, Optional<Void>> hyperwalletWebhookNotificationSenderStrategyFactory;

	public NotificationServiceImpl(
			final StrategyFactory<HyperwalletWebhookNotification, Optional<Void>> hyperwalletWebhookNotificationSenderStrategyFactory) {
		this.hyperwalletWebhookNotificationSenderStrategyFactory = hyperwalletWebhookNotificationSenderStrategyFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processNotification(final HyperwalletWebhookNotification incomingNotificationDTO) {
		this.hyperwalletWebhookNotificationSenderStrategyFactory.execute(incomingNotificationDTO);
	}

}
