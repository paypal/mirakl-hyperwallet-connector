package com.paypal.sellers.infrastructure.configuration;

import com.paypal.sellers.batchjobs.individuals.IndividualSellersExtractBatchJob;
import com.paypal.sellers.jobs.IndividualSellersExtractJob;
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
public class IndividualSellerExtractJobConfig {

	private static final String TRIGGER_SUFFIX = "Trigger";

	private static final String JOB_NAME = "IndividualSellersExtractJob";

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
	 * {@link IndividualSellerExtractJobConfig#sellerExtractJob()}
	 * @param jobDetails the {@link JobDetail}
	 * @return the {@link Trigger}
	 */
	@Bean
	public Trigger sellerExtractTrigger(@Qualifier("sellerExtractJob") final JobDetail jobDetails,
			@Value("${sellers.extractsellers.scheduling.cronexpression}") final String cronExpression) {
		//@formatter:off
        return TriggerBuilder.newTrigger()
                .forJob(jobDetails)
                .withIdentity(TRIGGER_SUFFIX + JOB_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        //@formatter:on
	}

}
