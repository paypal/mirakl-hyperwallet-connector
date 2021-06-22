package com.paypal.invoices.infraestructure.configuration;

import com.paypal.invoices.jobs.InvoicesExtractJob;
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
@PropertySource({ "classpath:invoices.properties" })
public class InvoicesExtractJobConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "InvoicesExtractJob";

	/**
	 * Creates a recurring job {@link InvoicesExtractJob}
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail invoicesExtractJob() {
		//@formatter:off
		return JobBuilder.newJob(InvoicesExtractJob.class)
				.withIdentity(JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	/**
	 * Schedules the recurring job {@link InvoicesExtractJob} with the {@code jobDetails}
	 * set on {@link InvoicesExtractJobConfig#invoicesExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger invoicesExtractTrigger(@Qualifier("invoicesExtractJob") final JobDetail jobDetails,
			@Value("${invoices.extractinvoices.scheduling.cronexpression}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
