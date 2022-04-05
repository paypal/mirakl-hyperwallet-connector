package com.paypal.sellers.infrastructure.configuration;

import com.paypal.sellers.jobs.ProfessionalSellersExtractJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellersExtractJobConfigTest {

	private static final String CRON_EXPRESSION = "0 0 0 1/1 * ? *";

	private static final String TRIGGER_PREFIX = "Trigger";

	private static final String JOB_NAME = "ProfessionalSellersExtractJob";

	@InjectMocks
	private ProfessionalSellerExtractJobConfig testObj;

	@Test
	void extractSellersJob_createsJobDetailWithNameExtractProfessionalSellersJobAndTypeExtractProfessionalSellersJob() {
		final JobDetail result = this.testObj.professionalSellerExtractJob();

		assertThat(result.getJobClass()).hasSameClassAs(ProfessionalSellersExtractJob.class);
		assertThat(result.getKey().getName()).isEqualTo("ProfessionalSellersExtractJob");
	}

	@Test
	void sellerExtractTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = JobBuilder.newJob(ProfessionalSellersExtractJob.class).withIdentity(JOB_NAME)
				.build();

		final Trigger result = this.testObj.professionalSellerExtractTrigger(jobDetail, CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREFIX + JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(CRON_EXPRESSION);
	}

}
