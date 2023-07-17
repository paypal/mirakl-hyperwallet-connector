package com.paypal.notifications.failures.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.failures.connectors.NotificationsRepository;
import com.paypal.notifications.failures.repositories.FailedNotificationInformationRepository;
import com.paypal.notifications.incoming.services.NotificationProcessingService;
import com.paypal.notifications.storage.repositories.entities.NotificationInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class FailedNotificationServiceImpl implements FailedNotificationService {

	private final NotificationsRepository notificationsRepository;

	private final FailedNotificationInformationRepository failedNotificationInformationRepository;

	private final NotificationProcessingService notificationProcessingService;

	public FailedNotificationServiceImpl(final NotificationsRepository notificationsRepository,
			final FailedNotificationInformationRepository failedNotificationInformationRepository,
			final NotificationProcessingService notificationProcessingService) {
		this.notificationsRepository = notificationsRepository;
		this.failedNotificationInformationRepository = failedNotificationInformationRepository;
		this.notificationProcessingService = notificationProcessingService;
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
				.forEach(notificationProcessingService::processNotification);
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
