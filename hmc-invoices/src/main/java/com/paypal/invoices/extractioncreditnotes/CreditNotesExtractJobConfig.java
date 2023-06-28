package com.paypal.invoices.extractioncreditnotes;

import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesRetryBatchJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreditNotesExtractJobConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String RETRY_JOB_NAME = "CreditNotesRetryJob";

	@Bean
	public JobDetail creditNotesRetryJob(final CreditNotesRetryBatchJob creditNotesRetryBatchJob) {
		//@formatter:off
		return QuartzBatchJobBuilder.newJob(creditNotesRetryBatchJob)
				.withIdentity(RETRY_JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	@Bean
	public Trigger creditNotesRetryTrigger(@Qualifier("creditNotesRetryJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.retry-jobs.creditnotes}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + RETRY_JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
