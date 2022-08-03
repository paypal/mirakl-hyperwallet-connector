package com.paypal.notifications.service.hmc.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import com.paypal.notifications.repository.NotificationsRepository;
import com.paypal.notifications.service.FailedNotificationService;
import com.paypal.notifications.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class FailedNotificationServiceImpl implements FailedNotificationService {

	private final NotificationsRepository notificationsRepository;

	private final FailedNotificationInformationRepository failedNotificationInformationRepository;

	private final NotificationService notificationService;

	public FailedNotificationServiceImpl(final NotificationsRepository notificationsRepository,
			final FailedNotificationInformationRepository failedNotificationInformationRepository,
			final NotificationService notificationService) {
		this.notificationsRepository = notificationsRepository;
		this.failedNotificationInformationRepository = failedNotificationInformationRepository;
		this.notificationService = notificationService;
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
				.forEach(notificationService::processNotification);
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
