package com.paypal.notifications.service.hmc.impl;

import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.repository.NotificationEntityRepository;
import com.paypal.notifications.service.hmc.NotificationEntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Default implementation of {@link NotificationEntityService}.
 */
@Slf4j
@Service
public class NotificationEntityServiceImpl implements NotificationEntityService {

	private final NotificationEntityRepository notificationEntityRepository;

	public NotificationEntityServiceImpl(final NotificationEntityRepository notificationEntityRepository) {
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
