package com.paypal.notifications.storage.jobs;

import com.paypal.jobsystem.quartzintegration.support.AbstractDeltaInfoJob;
import com.paypal.notifications.storage.services.NotificationCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;

/**
 * Quartz job that triggers the cleanup of expired {@code NotificationEntity} records.
 *
 * <p>
 * Only terminal-status notifications ({@code FAILED}, {@code SUCCESS}, {@code OUTDATED})
 * whose {@code receptionDate} is older than the configured retention period are removed.
 * By default the job runs once a day and the retention window is 90 days; both values are
 * configurable via {@code application.properties}.
 *
 * @see NotificationCleanupService
 * @see NotificationCleanupJobConfiguration
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class NotificationCleanupJob extends AbstractDeltaInfoJob {

	private final NotificationCleanupService notificationCleanupService;

	@Override
	public void execute(final JobExecutionContext context) {
		log.info("NotificationCleanupJob started");
		notificationCleanupService.deleteExpiredNotifications();
		log.info("NotificationCleanupJob finished");
	}

}
