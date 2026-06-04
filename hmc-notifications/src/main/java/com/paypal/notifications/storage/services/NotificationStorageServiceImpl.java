package com.paypal.notifications.storage.services;

import com.paypal.notifications.storage.repositories.NotificationEntityRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link NotificationStorageService}.
 */
@Slf4j
@Service
public class NotificationStorageServiceImpl implements NotificationStorageService {

	private final NotificationEntityRepository notificationEntityRepository;

	public NotificationStorageServiceImpl(final NotificationEntityRepository notificationEntityRepository) {
		this.notificationEntityRepository = notificationEntityRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationEntity saveNotification(final NotificationEntity notificationEntity) {
		log.debug("Saving notification with token [{}]", notificationEntity.getWebHookToken());
		return notificationEntityRepository.save(notificationEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NotificationEntity> getNotificationsBetween(final Date from, final Date to) {
		return notificationEntityRepository.findNotificationsBetween(from, to);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteNotificationsBetween(final Date from, final Date to) {
		notificationEntityRepository.deleteNotificationsBetween(from, to);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NotificationEntity> getNotificationsByWebHookToken(final String webHookToken) {
		return notificationEntityRepository.findNotificationsByWebHookToken(webHookToken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NotificationEntity> getNotificationsByObjectTokenAndAndCreationDateAfter(final String objectToken,
			final Date creationDate) {
		return notificationEntityRepository.findNotificationsByObjectTokenAndAndCreationDateAfter(objectToken,
				creationDate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<NotificationEntity> getFailedNotifications(final NotificationType notificationType,
			final String objectToken, final Pageable pageable) {
		return notificationEntityRepository.findFailedNotifications(notificationType, objectToken, pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean existsNewerNotificationByObjectToken(final String objectToken, final Date creationDate) {
		return notificationEntityRepository.existsNewerNotificationByObjectToken(objectToken, creationDate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<NotificationEntity> getNotificationByWebHookToken(final String webHookToken) {
		return notificationEntityRepository.findByWebHookToken(webHookToken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteNotificationByWebHookToken(final String webHookToken) {
		notificationEntityRepository.deleteByWebHookToken(webHookToken);
	}

}
