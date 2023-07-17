package com.paypal.sellers.stakeholdersextraction;

import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBuilder;
import com.paypal.sellers.professionalsellersextraction.ProfessionalSellersExtractionJobsConfig;
import com.paypal.sellers.stakeholdersextraction.batchjobs.BusinessStakeholdersRetryBatchJob;
import com.paypal.sellers.professionalsellersextraction.jobs.ProfessionalSellersExtractJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for scheduling jobs at startup
 */
@Configuration
public class BusinessStakeholdersExtractionJobsConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String RETRY_JOB_NAME = "BusinessStakeholdersRetryJob";

	@Bean
	public JobDetail businessStakeholdersRetryJob(
			final BusinessStakeholdersRetryBatchJob businessStakeholdersRetryBatchJob) {
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
	 * {@link ProfessionalSellersExtractionJobsConfig#professionalSellerExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger businessStakeholdersRetryTrigger(
			@Qualifier("businessStakeholdersRetryJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.retry-jobs.businessstakeholders}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + RETRY_JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
