package com.paypal.invoices.extractioninvoices;

import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesRetryBatchJob;
import com.paypal.invoices.extractioncommons.jobs.InvoicesExtractJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBuilder;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for scheduling jobs at startup
 */
@Slf4j
@Configuration
public class InvoicesExtractJobConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "InvoicesExtractJob";

	private static final String RETRY_JOB_NAME = "InvoicesRetryJob";

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
			@Value("${hmc.jobs.scheduling.extract-jobs.invoices}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

	@Bean
	public JobDetail invoicesRetryJob(final InvoicesRetryBatchJob invoicesRetryBatchJob) {
		//@formatter:off
		return QuartzBatchJobBuilder.newJob(invoicesRetryBatchJob)
				.withIdentity(RETRY_JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	@Bean
	public Trigger invoicesRetryTrigger(@Qualifier("invoicesRetryJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.retry-jobs.invoices}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + RETRY_JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
