package com.paypal.notifications.storage.services;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service for dealing with {@link NotificationEntity}.
 */
public interface NotificationStorageService {

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
	 * Returns {@code true} if at least one {@link NotificationEntity} exists whose
	 * {@code objectToken} equals the given one and {@code creationDate} is strictly after
	 * the given date.
	 * @param objectToken the objectToken to search for.
	 * @param creationDate the reference creation date.
	 * @return {@code true} if a newer notification for the same object exists.
	 */
	boolean existsNewerNotificationByObjectToken(final String objectToken, final Date creationDate);

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

	/**
	 * Retrieves a page of {@link NotificationEntity} with {@code FAILED} or
	 * {@code RETRYING} status, optionally filtered by type and object token (target).
	 * <p>
	 * {@code RETRYING} notifications are included so that callers can determine whether a
	 * notification is "fully failed" by comparing its retry counter against the
	 * configured maximum.
	 * @param notificationType optional type filter; {@code null} matches all types.
	 * @param objectToken optional object-token filter; {@code null} matches all tokens.
	 * @param pageable pagination and sort information.
	 * @return a {@link Page} of failed or retrying notifications.
	 */
	Page<NotificationEntity> getFailedNotifications(NotificationType notificationType, String objectToken,
			Pageable pageable);

	/**
	 * Retrieves a single {@link NotificationEntity} by its webhook token.
	 * @param webHookToken the webhook token to look up.
	 * @return an {@link Optional} containing the entity if found.
	 */
	Optional<NotificationEntity> getNotificationByWebHookToken(String webHookToken);

	/**
	 * Deletes the {@link NotificationEntity} identified by the given webhook token.
	 * @param webHookToken the webhook token of the notification to delete.
	 */
	void deleteNotificationByWebHookToken(String webHookToken);

}
