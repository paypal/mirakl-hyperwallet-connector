package com.paypal.sellers.infrastructure.configuration;

import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBuilder;
import com.paypal.sellers.batchjobs.bstk.BusinessStakeholdersRetryBatchJob;
import com.paypal.sellers.jobs.ProfessionalSellersExtractJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
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
public class BusinessStakeholdersExtractJobConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String RETRY_JOB_NAME = "BusinessStakeholdersRetryJob";

	@Bean
	public JobDetail businessStakeholdersRetryJob(BusinessStakeholdersRetryBatchJob businessStakeholdersRetryBatchJob) {
		//@formatter:off
		return QuartzBatchJobBuilder.newJob(businessStakeholdersRetryBatchJob)
				.withIdentity(RETRY_JOB_NAME)
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
	public Trigger businessStakeholdersRetryTrigger(
			@Qualifier("businessStakeholdersRetryJob") final JobDetail jobDetails,
			@Value("${sellers.retrybusinessstakeholders.scheduling.cronexpression}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + RETRY_JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
