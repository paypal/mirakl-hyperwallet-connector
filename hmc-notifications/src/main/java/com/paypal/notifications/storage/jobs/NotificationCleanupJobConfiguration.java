package com.paypal.notifications.storage.jobs;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz configuration for the {@link NotificationCleanupJob}.
 *
 * <p>
 * The cron expression is read from
 * {@code hmc.jobs.scheduling.notifications-cleanup-job.cron}, which can be overridden via
 * the {@code PAYPAL_HYPERWALLET_NOTIFICATIONS_CLEANUP_CRON_EXPRESSION} environment
 * variable. The default schedule runs the job once per day at 01:00 UTC.
 */
@Configuration
public class NotificationCleanupJobConfiguration {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "NotificationCleanupJob";

	/**
	 * Creates the durable {@link JobDetail} for the {@link NotificationCleanupJob}.
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail notificationCleanupJobDetail() {
		//@formatter:off
		return JobBuilder.newJob(NotificationCleanupJob.class)
				.withIdentity(JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	/**
	 * Schedules the {@link NotificationCleanupJob} using the cron expression from
	 * {@code hmc.jobs.scheduling.notifications-cleanup-job.cron}.
	 * @param jobDetail the {@link JobDetail} produced by
	 * {@link #notificationCleanupJobDetail()}
	 * @param cronExpression the Quartz cron expression
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger notificationCleanupJobTrigger(@Qualifier("notificationCleanupJobDetail") final JobDetail jobDetail,
			@Value("${hmc.jobs.scheduling.notifications-cleanup-job.cron}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetail)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
