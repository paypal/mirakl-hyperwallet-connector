package com.paypal.reports.infraestructure.configuration;

import com.paypal.reports.jobs.ReportsExtractJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for scheduling jobs at startup
 */
@Slf4j
@Configuration
@PropertySource({ "classpath:reports.properties" })
public class ReportsExtractJobConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "ReportsExtractJob";

	/**
	 * Creates a recurring job {@link ReportsExtractJob}
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail reportsExtractJob() {
		//@formatter:off
        return JobBuilder.newJob(ReportsExtractJob.class)
                .withIdentity(JOB_NAME)
                .storeDurably()
                .build();
        //@formatter:on
	}

	/**
	 * Schedules the recurring job {@link ReportsExtractJob} with the {@code jobDetails}
	 * set on {@link ReportsExtractJobConfig#reportsExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger reportsExtractTrigger(@Qualifier("reportsExtractJob") final JobDetail jobDetails,
			@Value("${reports.extractreports.scheduling.cronexpression}") final String cronExpression) {
		//@formatter:off
        return TriggerBuilder.newTrigger()
                .forJob(jobDetails)
                .withIdentity(TRIGGER_SUFFIX + JOB_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        //@formatter:on
	}

}
