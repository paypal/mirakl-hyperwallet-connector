package com.paypal.notifications.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.notifications.repository.NotificationsRepository;
import com.paypal.notifications.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationsRepository notificationsRepository;

	private final FailedNotificationInformationRepository failedNotificationInformationRepository;

	private final StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutor;

	public NotificationServiceImpl(final NotificationsRepository notificationsRepository,
			final FailedNotificationInformationRepository failedNotificationInformationRepository,
			final StrategyExecutor<HyperwalletWebhookNotification, Void> hyperwalletWebhookNotificationSenderStrategyExecutor) {
		this.notificationsRepository = notificationsRepository;
		this.failedNotificationInformationRepository = failedNotificationInformationRepository;
		this.hyperwalletWebhookNotificationSenderStrategyExecutor = hyperwalletWebhookNotificationSenderStrategyExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processNotification(final HyperwalletWebhookNotification incomingNotificationDTO) {
		hyperwalletWebhookNotificationSenderStrategyExecutor.execute(incomingNotificationDTO);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processFailedNotifications() {
		final Iterable<NotificationInfoEntity> failedNotifications = failedNotificationInformationRepository.findAll();
		//@formatter:off
		StreamSupport.stream(failedNotifications.spliterator(), false)
				.map(this::getNotificationToReprocess)
				.filter(Objects::nonNull)
				.forEach(this::processNotification);
		//@formatter:on
	}

	private HyperwalletWebhookNotification getNotificationToReprocess(
			final NotificationInfoEntity failedNotificationInformationEntity) {
		log.info("Reprocessing notification [{}]", failedNotificationInformationEntity.getNotificationToken());
		return notificationsRepository.getHyperwalletWebhookNotification(
				failedNotificationInformationEntity.getProgram(),
				failedNotificationInformationEntity.getNotificationToken());
	}

}
