package com.paypal.kyc.statussynchronization;

import com.paypal.kyc.documentextractioncommons.DocumentsExtractionJobsConfig;
import com.paypal.kyc.documentextractioncommons.jobs.DocumentsExtractJob;
import com.paypal.kyc.statussynchronization.jobs.KYCUserStatusResyncJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatusSynchronizationJobsConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "KYCUserStatusInfoJob";

	@Value("${hmc.toggle-features.resync-jobs}")
	private boolean resyncJobsEnabled;

	/**
	 * Creates a recurring job {@link DocumentsExtractJob}
	 * @return the {@link JobDetail}
	 */
	@Bean
	public JobDetail kycUserStatusInfoJob() {
		//@formatter:off
		return JobBuilder.newJob(KYCUserStatusResyncJob.class)
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
	public Trigger kycUserStatusInfoJobTrigger(@Qualifier("kycUserStatusInfoJob") final JobDetail jobDetails,
			@Value("${hmc.jobs.scheduling.resync-jobs.kycstatus}") final String cronExpression) {
		if (!resyncJobsEnabled) {
			return null;
		}

		//@formatter:off
		return TriggerBuilder.newTrigger()
				.forJob(jobDetails)
				.withIdentity(TRIGGER_SUFFIX + JOB_NAME)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
		//@formatter:on
	}

}
