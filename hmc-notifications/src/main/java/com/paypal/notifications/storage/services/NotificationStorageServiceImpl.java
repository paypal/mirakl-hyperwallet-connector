package com.paypal.notifications.storage.services;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.NotificationEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

}
