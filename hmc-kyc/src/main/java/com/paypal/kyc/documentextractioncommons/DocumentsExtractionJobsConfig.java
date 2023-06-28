package com.paypal.kyc.documentextractioncommons;

import com.paypal.kyc.documentextractioncommons.jobs.DocumentsExtractJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for scheduling jobs at startup
 */
@Configuration
public class DocumentsExtractionJobsConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "DocumentsExtractJob";

	/**
	 * Creates a recurring job {@link DocumentsExtractJob}
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail documentsExtractJob() {
		//@formatter:off
		return JobBuilder.newJob(DocumentsExtractJob.class)
				.withIdentity(JOB_NAME)
				.storeDurably()
				.build();
		//@formatter:on
	}

	/**
	 * Schedules the recurring job {@link DocumentsExtractJob} with the {@code jobDetails}
	 * set on {@link DocumentsExtractionJobsConfig#documentsExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger documentsExtractJobTrigger(@Qualifier("documentsExtractJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.extract-jobs.kycdocuments}") final String cronExpression) {
		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
