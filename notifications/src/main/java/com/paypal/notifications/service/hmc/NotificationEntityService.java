package com.paypal.notifications.service.hmc;

import com.paypal.notifications.model.entity.NotificationEntity;

import java.util.Date;
import java.util.List;

/**
 * Service for dealing with {@link NotificationEntity}.
 */
public interface NotificationEntityService {

	/**
	 * Save the given {@link NotificationEntity} in DB.
	 * @param notificationEntity a {@link NotificationEntity}.
	 * @return the saved notification.
	 */
	NotificationEntity saveNotification(final NotificationEntity notificationEntity);

	/**
	 * Retrieves all the {@link NotificationEntity} whose date are between the given
	 * dates.
	 * @param from from {@link Date}.
	 * @param to to {@link Date}.
	 * @return a {@link List} of {@link NotificationEntity} whose reception date are
	 * between the given from and to dates.
	 */
	List<NotificationEntity> getNotificationsBetween(final Date from, final Date to);

	/**
	 * Deletes all the {@link NotificationEntity} whose reception date are between the
	 * given dates.
	 * @param from from {@link Date}.
	 * @param to to {@link Date}.
	 */
	void deleteNotificationsBetween(final Date from, final Date to);

	/**
	 * Retrieves all the {@link NotificationEntity} whose webHookToken is equals to the
	 * given one.
	 * @param webHookToken the webHookToken to search for.
	 * @return a {@link List} of {@link NotificationEntity} whose webHookToken is equals
	 * to the given one.
	 */
	List<NotificationEntity> getNotificationsByWebHookToken(final String webHookToken);

	/**
	 * Retrieves all the {@link NotificationEntity} whose objectToken is equals to the
	 * given one and creationDate is later than the given one.
	 * @param objectToken the objectToken to search for.
	 * @param creationDate the creationDate to search for.
	 * @return a {@link List} of {@link NotificationEntity} whose objectToken is equals to
	 * the given one and creationDate is later than the given one.
	 */
	List<NotificationEntity> getNotificationsByObjectTokenAndAndCreationDateAfter(final String objectToken,
			final Date creationDate);

}
