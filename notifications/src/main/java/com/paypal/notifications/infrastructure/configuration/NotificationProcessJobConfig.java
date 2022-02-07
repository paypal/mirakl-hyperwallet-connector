package com.paypal.notifications.infrastructure.configuration;

import com.paypal.notifications.jobs.NotificationProcessJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({ "classpath:notifications.properties" })
public class NotificationProcessJobConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "NotificationProcessJob";

	/**
	 * Creates a recurring job {@link NotificationProcessJob}
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail notificationProcessJob() {
		//@formatter:off
        return JobBuilder.newJob(NotificationProcessJob.class)
                .withIdentity(JOB_NAME)
                .storeDurably()
                .build();
        //@formatter:on
	}

	/**
	 * Schedules the recurring job {@link NotificationProcessJob} with the
	 * {@code jobDetails} set on
	 * {@link NotificationProcessJobConfig#notificationProcessJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger notificationProcessTrigger(@Qualifier("notificationProcessJob") final JobDetail jobDetails,
			@Value("${notifications.retryfailed.scheduling.cronexpression}") final String cronExpression) {
		//@formatter:off
        return TriggerBuilder.newTrigger()
                .forJob(jobDetails)
                .withIdentity(TRIGGER_SUFFIX + JOB_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        //@formatter:on
	}

}
