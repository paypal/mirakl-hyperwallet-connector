package com.paypal.sellers.infrastructure.configuration;

import com.paypal.sellers.jobs.IndividualSellersExtractJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class IndividualSellersExtractJobConfigTest {

	private static final String CRON_EXPRESSION = "0 0 0 1/1 * ? *";

	private static final String TRIGGER_PREXIX = "Trigger";

	private static final String JOB_NAME = "IndividualSellersExtractJob";

	@InjectMocks
	private IndividualSellerExtractJobConfig testObj;

	@Test
	void extractSellersJob_createsJobDetailWithNameExtractSellersJobAndTypeExtractSellersJob() {
		final JobDetail result = testObj.sellerExtractJob();

		assertThat(result.getJobClass()).hasSameClassAs(IndividualSellersExtractJob.class);
		assertThat(result.getKey().getName()).isEqualTo("IndividualSellersExtractJob");
	}

	@Test
	void sellerExtractTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = JobBuilder.newJob(IndividualSellersExtractJob.class).withIdentity(JOB_NAME).build();

		final Trigger result = testObj.sellerExtractTrigger(jobDetail, CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
	}

}
