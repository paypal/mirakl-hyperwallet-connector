package com.paypal.sellers.infrastructure.configuration;

import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBuilder;
import com.paypal.sellers.batchjobs.bankaccount.BankAccountRetryBatchJob;
import com.paypal.sellers.jobs.BankAccountExtractJob;
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
public class BankAccountExtractJobConfig {

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
	 * {@link BankAccountExtractJobConfig#bankAccountExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger bankAccountExtractJobTrigger(@Qualifier("bankAccountExtractJob") final JobDetail jobDetails,
			@Value("${sellers.bankaccountextract.scheduling.cronexpression}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

	@Bean
	public JobDetail bankAccountRetryJob(BankAccountRetryBatchJob bankAccountRetryBatchJob) {
		//@formatter:off
		return QuartzBatchJobBuilder.newJob(bankAccountRetryBatchJob)
				.withIdentity(RETRY_JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	@Bean
	public Trigger bankAccountRetryJobTrigger(@Qualifier("bankAccountRetryJob") final JobDetail jobDetails,
			@Value("${sellers.bankaccountretry.scheduling.cronexpression}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + RETRY_JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
