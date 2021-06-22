package com.paypal.sellers.infrastructure.configuration;

import com.paypal.sellers.jobs.ProfessionalSellersExtractJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for scheduling jobs at startup
 */
@Configuration
@PropertySource({ "classpath:sellers.properties" })
public class ProfessionalSellerExtractJobConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "ProfessionalSellersExtractJob";

	/**
	 * Creates a recurring job {@link ProfessionalSellersExtractJob}
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail professionalSellerExtractJob() {
		//@formatter:off
		return JobBuilder.newJob(ProfessionalSellersExtractJob.class)
				.withIdentity(JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	/**
	 * Schedules the recurring job {@link ProfessionalSellersExtractJob} with the
	 * {@code jobDetails} set on
	 * {@link ProfessionalSellerExtractJobConfig#professionalSellerExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger professionalSellerExtractTrigger(
			@Qualifier("professionalSellerExtractJob") final JobDetail jobDetails,
			@Value("${sellers.extractprofessionalsellers.scheduling.cronexpression}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
