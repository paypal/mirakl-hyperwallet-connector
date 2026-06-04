package com.paypal.notifications.incoming.jobs;

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
 * Quartz configuration for the {@link NotificationProcessJob}.
 */
@Configuration
public class NotificationProcessJobConfiguration {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "NotificationProcessJob";

	/**
	 * Creates a recurring {@link NotificationProcessJob} job detail.
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail notificationProcessJobDetail() {
		//@formatter:off
		return JobBuilder.newJob(NotificationProcessJob.class)
				.withIdentity(JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	/**
	 * Schedules the {@link NotificationProcessJob} with the cron expression from
	 * {@code hmc.jobs.scheduling.notifications-processing-job.webhooks}.
	 * @param jobDetail the {@link JobDetail}
	 * @param cronExpression the cron expression
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger notificationProcessJobTrigger(@Qualifier("notificationProcessJobDetail") final JobDetail jobDetail,
			@Value("${hmc.jobs.scheduling.notifications-processing-job.webhooks}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetail)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
