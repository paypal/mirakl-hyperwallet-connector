package com.paypal.sellers.professionalsellersextraction;

import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBuilder;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersRetryBatchJob;
import com.paypal.sellers.professionalsellersextraction.jobs.ProfessionalSellersExtractJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for scheduling jobs at startup
 */
@Configuration
public class ProfessionalSellersExtractionJobsConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "ProfessionalSellersExtractJob";

	private static final String RETRY_JOB_NAME = "ProfessionalSellersRetryJob";

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
	 * {@link ProfessionalSellersExtractionJobsConfig#professionalSellerExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger professionalSellerExtractTrigger(
			@Qualifier("professionalSellerExtractJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.extract-jobs.professionalsellers}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

	@Bean
	public JobDetail professionalSellerRetryJob(
			final ProfessionalSellersRetryBatchJob professionalSellersRetryBatchJob) {
		//@formatter:off
		return QuartzBatchJobBuilder.newJob(professionalSellersRetryBatchJob)
				.withIdentity(RETRY_JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	@Bean
	public Trigger professionalSellerRetryTrigger(@Qualifier("professionalSellerRetryJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.retry-jobs.professionalsellers}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + RETRY_JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
