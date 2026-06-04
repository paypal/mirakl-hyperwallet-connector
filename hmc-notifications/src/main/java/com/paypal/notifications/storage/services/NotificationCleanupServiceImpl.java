package com.paypal.notifications.storage.services;

import com.paypal.notifications.storage.repositories.NotificationEntityRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.EnumSet;

/**
 * Default implementation of {@link NotificationCleanupService}.
 *
 * <p>
 * Deletes {@code NotificationEntity} rows that simultaneously satisfy:
 * <ol>
 * <li>Their {@code receptionDate} is older than
 * {@code hmc.notifications.cleanup.retention-days} days (default: 90).</li>
 * <li>Their {@code status} is one of {@code FAILED}, {@code SUCCESS}, or
 * {@code OUTDATED}.</li>
 * </ol>
 *
 * <p>
 * Both the retention period and the job schedule are configurable via
 * {@code application.properties} and can be overridden through environment variables.
 */
@Slf4j
@Service
public class NotificationCleanupServiceImpl implements NotificationCleanupService {

	private static final EnumSet<NotificationStatus> CLEANUP_STATUSES = EnumSet.of(NotificationStatus.FAILED,
			NotificationStatus.SUCCESS, NotificationStatus.OUTDATED);

	private final NotificationEntityRepository notificationEntityRepository;

	private final int retentionDays;

	public NotificationCleanupServiceImpl(final NotificationEntityRepository notificationEntityRepository,
			@Value("${hmc.notifications.cleanup.retention-days:90}") final int retentionDays) {
		this.notificationEntityRepository = notificationEntityRepository;
		this.retentionDays = retentionDays;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteExpiredNotifications() {
		final Date cutoffDate = Date.from(Instant.now().minus(retentionDays, ChronoUnit.DAYS));
		log.info("Deleting notifications with status {} received before [{}]", CLEANUP_STATUSES, cutoffDate);
		notificationEntityRepository.deleteByReceptionDateBeforeAndStatusIn(cutoffDate, CLEANUP_STATUSES);
		log.info("Notification cleanup completed successfully");
	}

}
