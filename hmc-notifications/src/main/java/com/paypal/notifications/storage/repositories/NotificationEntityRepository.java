package com.paypal.notifications.storage.repositories;

import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Repository for retrieving {@link NotificationEntity} from repository.
 */
@Repository
@Transactional
public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, Long> {

	/**
	 * Retrieves all the {@link NotificationEntity} whose reception date are between the
	 * given dates.
	 * @param from from {@link Date}.
	 * @param to to {@link Date}.
	 * @return a {@link List} of {@link NotificationEntity} whose date are between the
	 * given dates.
	 */
	@Query("Select n from NotificationEntity n where n.receptionDate >= :from and n.receptionDate <= :to")
	List<NotificationEntity> findNotificationsBetween(@Param("from") Date from, @Param("to") Date to);

	/**
	 * Deletes all the {@link NotificationEntity} whose reception date are between the
	 * given dates.
	 * @param from from {@link Date}.
	 * @param to to {@link Date}.
	 */
	@Modifying
	@Query("Delete from NotificationEntity n where n.receptionDate >= :from and n.receptionDate <= :to")
	void deleteNotificationsBetween(@Param("from") Date from, @Param("to") Date to);

	/**
	 * Retrieves a page of {@link NotificationEntity} with {@code FAILED} or
	 * {@code RETRYING} status, optionally filtered by {@code notificationType} and
	 * {@code objectToken}.
	 * <p>
	 * {@code RETRYING} notifications are included so that callers can inspect in-progress
	 * failure recovery and determine whether a notification is "fully failed" based on
	 * its retry counter relative to the configured maximum.
	 * <p>
	 * A {@code null} value for either filter parameter means "no filter on that field".
	 * @param notificationType optional filter on notification type; {@code null} matches
	 * all types.
	 * @param objectToken optional filter on object token; {@code null} matches all
	 * tokens.
	 * @param pageable pagination and sort information.
	 * @return a {@link Page} of failed or retrying {@link NotificationEntity} matching
	 * the filters.
	 */
	@Query("Select n from NotificationEntity n"
			+ " where (n.status = com.paypal.notifications.storage.repositories.entities.NotificationStatus.FAILED"
			+ " or n.status = com.paypal.notifications.storage.repositories.entities.NotificationStatus.RETRYING)"
			+ " and (:notificationType is null or n.notificationType = :notificationType)"
			+ " and (:objectToken is null or n.objectToken = :objectToken)")
	Page<NotificationEntity> findFailedNotifications(@Param("notificationType") NotificationType notificationType,
			@Param("objectToken") String objectToken, Pageable pageable);

	/**
	 * Retrieves a {@link NotificationEntity} by its webhook token.
	 * @param webHookToken the webhook token.
	 * @return an {@link java.util.Optional} containing the entity if found.
	 */
	@Query("Select n from NotificationEntity n where n.webHookToken = :webHookToken")
	java.util.Optional<NotificationEntity> findByWebHookToken(@Param("webHookToken") String webHookToken);

	/**
	 * Deletes the {@link NotificationEntity} identified by the given webhook token.
	 * @param webHookToken the webhook token of the notification to delete.
	 */
	@Modifying
	@Query("Delete from NotificationEntity n where n.webHookToken = :webHookToken")
	void deleteByWebHookToken(@Param("webHookToken") String webHookToken);

	/**
	 * Retrieves all the {@link NotificationEntity} whose webHookToken is equals to the
	 * given one.
	 * @param webHookToken the webHookToken to search for.
	 * @return a {@link List} of {@link NotificationEntity} whose webHookToken is equals
	 * to the given one.
	 */
	@Query("Select n from NotificationEntity n where n.webHookToken = :webHookToken")
	List<NotificationEntity> findNotificationsByWebHookToken(@Param("webHookToken") final String webHookToken);

	/**
	 * Returns {@code true} if at least one {@link NotificationEntity} exists whose
	 * {@code objectToken} equals the given one and {@code creationDate} is strictly after
	 * the given date.
	 * @param objectToken the objectToken to search for.
	 * @param creationDate the reference creation date.
	 * @return {@code true} if a newer notification for the same object exists.
	 */
	@Query("Select count(n) > 0 from NotificationEntity n where n.objectToken = :objectToken and n.creationDate > :creationDate")
	boolean existsNewerNotificationByObjectToken(@Param("objectToken") final String objectToken,
			@Param("creationDate") final Date creationDate);

	/**
	 * Retrieves all the {@link NotificationEntity} whose objectToken is equals to the
	 * given one and creationDate is later than the given one.
	 * @param objectToken the objectToken to search for.
	 * @param creationDate the creationDate to search for.
	 * @return a {@link List} of {@link NotificationEntity} whose objectToken is equals to
	 * the given one and creationDate is later than the given one.
	 */
	@Query("Select n from NotificationEntity n where n.objectToken = :objectToken and  n.creationDate > :creationDate")
	List<NotificationEntity> findNotificationsByObjectTokenAndAndCreationDateAfter(
			@Param("objectToken") final String objectToken, @Param("creationDate") final Date creationDate);

	/**
	 * Retrieves all {@link NotificationEntity} that are superseded by a newer
	 * notification for the same {@code objectToken} and {@code notificationType}. Only
	 * notifications still in an active state ({@code PENDING} or {@code RETRYING}) are
	 * returned — terminal states ({@code SUCCESS}, {@code FAILED}, {@code OUTDATED}) are
	 * excluded because they no longer need to be processed.
	 * @param objectToken the object token identifying the subject entity (e.g. a user or
	 * payment token).
	 * @param notificationType the type of notification.
	 * @param creationDate the creation date of the incoming notification; only existing
	 * notifications whose {@code creationDate} is strictly before this value are
	 * considered superseded.
	 * @return a {@link List} of active {@link NotificationEntity} instances that have
	 * been superseded.
	 */
	@Query("Select n from NotificationEntity n where n.objectToken = :objectToken"
			+ " and n.notificationType = :notificationType" + " and n.creationDate < :creationDate"
			+ " and (n.status = com.paypal.notifications.storage.repositories.entities.NotificationStatus.PENDING"
			+ " or n.status = com.paypal.notifications.storage.repositories.entities.NotificationStatus.RETRYING)")
	List<NotificationEntity> findActiveNotificationsSupersededBy(@Param("objectToken") String objectToken,
			@Param("notificationType") NotificationType notificationType, @Param("creationDate") Date creationDate);

	/**
	 * Retrieves the next batch of {@link NotificationEntity} instances eligible for
	 * processing, ordered by creation date ascending. Eligibility rules:
	 * <ul>
	 * <li>{@code PENDING} notifications are always eligible.</li>
	 * <li>{@code RETRYING} notifications are only eligible when their precomputed
	 * {@code nextRetryDate} is before {@code now} (i.e. the exponential back-off window
	 * has elapsed).</li>
	 * </ul>
	 * @param now the current timestamp; {@code RETRYING} notifications whose
	 * {@code nextRetryDate} is on or after this value are excluded.
	 * @param pageable pagination/limit information.
	 * @return a {@link List} of {@link NotificationEntity} ready for processing.
	 */
	@Query("Select n from NotificationEntity n where n.status = com.paypal.notifications.storage.repositories.entities.NotificationStatus.PENDING"
			+ " or (n.status = com.paypal.notifications.storage.repositories.entities.NotificationStatus.RETRYING"
			+ " and n.nextRetryDate < :now) order by n.creationDate asc")
	List<NotificationEntity> findNextBatchForProcessing(@Param("now") Date now, Pageable pageable);

	/**
	 * Deletes all {@link NotificationEntity} whose {@code receptionDate} is strictly
	 * before {@code cutoffDate} and whose {@code status} belongs to the provided
	 * collection. Only terminal-status notifications that have been sitting in the table
	 * longer than the configured retention period are affected.
	 * @param cutoffDate upper-exclusive date boundary; notifications received before this
	 * instant are candidates for deletion.
	 * @param statuses the set of {@link NotificationStatus} values that qualify for
	 * deletion (e.g. {@code FAILED}, {@code SUCCESS}, {@code OUTDATED}).
	 */
	@Modifying
	@Query("Delete from NotificationEntity n where n.receptionDate < :cutoffDate and n.status in :statuses")
	void deleteByReceptionDateBeforeAndStatusIn(@Param("cutoffDate") Date cutoffDate,
			@Param("statuses") Collection<NotificationStatus> statuses);

	/**
	 * Bulk-updates the status of a notification identified by its webhook token.
	 * @param webHookToken the webhook token.
	 * @param status the new {@link NotificationStatus}.
	 */
	@Modifying
	@Query("Update NotificationEntity n set n.status = :status where n.webHookToken = :webHookToken")
	void updateStatusByWebHookToken(@Param("webHookToken") String webHookToken,
			@Param("status") NotificationStatus status);

	/**
	 * Updates the status, retry counter, last-retry timestamp, and next-retry timestamp
	 * of a notification. Used for both {@link NotificationStatus#RETRYING} (with a
	 * computed {@code nextRetryDate} based on exponential back-off) and
	 * {@link NotificationStatus#FAILED} (with a {@code null} {@code nextRetryDate} since
	 * the notification will not be retried again).
	 * @param webHookToken the webhook token.
	 * @param status the new {@link NotificationStatus}.
	 * @param retryCounter the updated retry counter value.
	 * @param lastRetryDate the timestamp of this retry attempt.
	 * @param nextRetryDate the earliest timestamp at which this notification may be
	 * retried again, or {@code null} if it will not be retried.
	 */
	@Modifying
	@Query("Update NotificationEntity n set n.status = :status, n.retryCounter = :retryCounter,"
			+ " n.lastRetryDate = :lastRetryDate, n.nextRetryDate = :nextRetryDate"
			+ " where n.webHookToken = :webHookToken")
	void updateStatusAndRetryCounterByWebHookToken(@Param("webHookToken") String webHookToken,
			@Param("status") NotificationStatus status, @Param("retryCounter") int retryCounter,
			@Param("lastRetryDate") Date lastRetryDate, @Param("nextRetryDate") Date nextRetryDate);

}
