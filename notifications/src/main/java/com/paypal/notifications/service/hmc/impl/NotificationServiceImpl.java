package com.paypal.notifications.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.notifications.converter.NotificationConverter;
import com.paypal.notifications.evaluator.NotificationEntityEvaluator;
import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.service.NotificationService;
import com.paypal.notifications.service.hmc.NotificationEntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationConverter notificationConverter;

	private final NotificationEntityService notificationEntityService;

	private final NotificationEntityEvaluator notificationEntityEvaluator;

	private final StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutor;

	public NotificationServiceImpl(NotificationConverter notificationConverter,
			NotificationEntityService notificationEntityService,
			NotificationEntityEvaluator notificationEntityEvaluator,
			final StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutor) {
		this.notificationConverter = notificationConverter;
		this.notificationEntityService = notificationEntityService;
		this.notificationEntityEvaluator = notificationEntityEvaluator;
		this.hyperwalletWebhookNotificationSenderStrategyExecutor = hyperwalletWebhookNotificationSenderStrategyExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processNotification(final HyperwalletWebhookNotification incomingNotificationDTO) {
		final NotificationEntity notificationEntity = notificationConverter.convert(incomingNotificationDTO);

		notificationEntityService.saveNotification(notificationEntity);

		if (notificationEntityEvaluator.isProcessable(notificationEntity)) {
			hyperwalletWebhookNotificationSenderStrategyExecutor.execute(incomingNotificationDTO);
		}
	}

}
