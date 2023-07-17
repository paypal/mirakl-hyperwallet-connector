package com.paypal.sellers.individualsellersextraction;

import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBuilder;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersExtractBatchJob;
import com.paypal.sellers.individualsellersextraction.batchjobs.IndividualSellersRetryBatchJob;
import com.paypal.sellers.individualsellersextraction.jobs.IndividualSellersExtractJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for scheduling jobs at startup
 */
@Configuration
public class IndividualSellersExtractionJobsConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "IndividualSellersExtractJob";

	private static final String RETRY_JOB_NAME = "IndividualSellersExtractRetryJob";

	/**
	 * Creates a recurring job {@link IndividualSellersExtractBatchJob}
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail sellerExtractJob() {
		//@formatter:off
		return JobBuilder.newJob(IndividualSellersExtractJob.class)
				.withIdentity(JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	/**
	 * Schedules the recurring job {@link IndividualSellersExtractJob} with the
	 * {@code jobDetails} set on
	 * {@link IndividualSellersExtractionJobsConfig#sellerExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger sellerExtractTrigger(@Qualifier("sellerExtractJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.extract-jobs.sellers}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

	/**
	 * Creates a recurring job {@link IndividualSellersRetryBatchJob}
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail sellerExtractRetryJob(final IndividualSellersRetryBatchJob individualSellersRetryBatchJob) {
		//@formatter:off
		return QuartzBatchJobBuilder.newJob(individualSellersRetryBatchJob)
				.withIdentity(RETRY_JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	/**
	 * Schedules the recurring job {@link IndividualSellersRetryBatchJob} with the
	 * {@code jobDetails} set on
	 * {@link IndividualSellersExtractionJobsConfig#sellerExtractRetryJob(IndividualSellersRetryBatchJob)}
	 * ()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger sellerExtractRetryTrigger(@Qualifier("sellerExtractRetryJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.retry-jobs.sellers}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + RETRY_JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
