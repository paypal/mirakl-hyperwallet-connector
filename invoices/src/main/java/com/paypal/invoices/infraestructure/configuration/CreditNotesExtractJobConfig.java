package com.paypal.invoices.infraestructure.configuration;

import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBuilder;
import com.paypal.invoices.batchjobs.creditnotes.CreditNotesRetryBatchJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({ "classpath:invoices.properties" })
public class CreditNotesExtractJobConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String RETRY_JOB_NAME = "CreditNotesRetryJob";

	@Bean
	public JobDetail creditNotesRetryJob(CreditNotesRetryBatchJob creditNotesRetryBatchJob) {
		//@formatter:off
		return QuartzBatchJobBuilder.newJob(creditNotesRetryBatchJob)
				.withIdentity(RETRY_JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	@Bean
	public Trigger creditNotesRetryTrigger(@Qualifier("creditNotesRetryJob") final JobDetail jobDetails,
			@Value("${creditnotes.retryinvoices.scheduling.cronexpression}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + RETRY_JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
