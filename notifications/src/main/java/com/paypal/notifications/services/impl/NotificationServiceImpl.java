package com.paypal.notifications.services.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.notifications.services.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutor;

	public NotificationServiceImpl(
			final StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutor) {
		this.hyperwalletWebhookNotificationSenderStrategyExecutor = hyperwalletWebhookNotificationSenderStrategyExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processNotification(final HyperwalletWebhookNotification incomingNotificationDTO) {
		this.hyperwalletWebhookNotificationSenderStrategyExecutor.execute(incomingNotificationDTO);
	}

}
