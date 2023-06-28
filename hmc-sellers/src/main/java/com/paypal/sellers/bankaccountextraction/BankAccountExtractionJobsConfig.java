package com.paypal.sellers.bankaccountextraction;

import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBuilder;
import com.paypal.sellers.bankaccountextraction.batchjobs.BankAccountRetryBatchJob;
import com.paypal.sellers.bankaccountextraction.jobs.BankAccountExtractJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for scheduling jobs at startup
 */
@Configuration
public class BankAccountExtractionJobsConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "BankAccountExtractJob";

	private static final String RETRY_JOB_NAME = "BankAccountRetryJob";

	/**
	 * Creates a recurring job {@link BankAccountExtractJob}
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail bankAccountExtractJob() {
		//@formatter:off
		return JobBuilder.newJob(BankAccountExtractJob.class)
				.withIdentity(JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	/**
	 * Schedules the recurring job {@link BankAccountExtractJob} with the
	 * {@code jobDetails} set on
	 * {@link BankAccountExtractionJobsConfig#bankAccountExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger bankAccountExtractJobTrigger(@Qualifier("bankAccountExtractJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.extract-jobs.bankaccounts}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

	@Bean
	public JobDetail bankAccountRetryJob(final BankAccountRetryBatchJob bankAccountRetryBatchJob) {
		//@formatter:off
		return QuartzBatchJobBuilder.newJob(bankAccountRetryBatchJob)
				.withIdentity(RETRY_JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	@Bean
	public Trigger bankAccountRetryJobTrigger(@Qualifier("bankAccountRetryJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.retry-jobs.bankaccounts}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + RETRY_JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
