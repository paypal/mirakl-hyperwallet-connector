package com.paypal.notifications.incoming.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.notifications.storage.services.NotificationStorageService;
import com.paypal.notifications.incoming.services.converters.NotificationConverter;
import com.paypal.notifications.incoming.services.evaluators.NotificationEntityEvaluator;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationProcessingServiceImpl implements NotificationProcessingService {

	private final NotificationConverter notificationConverter;

	private final NotificationStorageService notificationStorageService;

	private final NotificationEntityEvaluator notificationEntityEvaluator;

	private final StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutor;

	public NotificationProcessingServiceImpl(final NotificationConverter notificationConverter,
			final NotificationStorageService notificationStorageService,
			final NotificationEntityEvaluator notificationEntityEvaluator,
			final StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutor) {
		this.notificationConverter = notificationConverter;
		this.notificationStorageService = notificationStorageService;
		this.notificationEntityEvaluator = notificationEntityEvaluator;
		this.hyperwalletWebhookNotificationSenderStrategyExecutor = hyperwalletWebhookNotificationSenderStrategyExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processNotification(final HyperwalletWebhookNotification incomingNotificationDTO) {
		final NotificationEntity notificationEntity = notificationConverter.convert(incomingNotificationDTO);

		notificationStorageService.saveNotification(notificationEntity);

		if (notificationEntityEvaluator.isProcessable(notificationEntity)) {
			hyperwalletWebhookNotificationSenderStrategyExecutor.execute(incomingNotificationDTO);
		}
	}

}
