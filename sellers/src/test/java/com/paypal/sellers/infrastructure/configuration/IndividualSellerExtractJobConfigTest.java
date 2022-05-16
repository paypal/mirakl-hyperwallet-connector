package com.paypal.sellers.infrastructure.configuration;

import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBean;
import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBuilder;
import com.paypal.sellers.batchjobs.individuals.IndividualSellersRetryBatchJob;
import com.paypal.sellers.jobs.IndividualSellersExtractJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class IndividualSellersExtractJobConfigTest {

	private static final String CRON_EXPRESSION = "0 0 0 1/1 * ? *";

	private static final String JOB_NAME = "IndividualSellersExtractJob";

	private static final String INDIVIDUAL_SELLERS_EXTRACT_RETRY_JOB_IDENTITY = "IndividualSellersExtractRetryJob";

	@InjectMocks
	private IndividualSellerExtractJobConfig testObj;

	@Mock
	private IndividualSellersRetryBatchJob individualSellersRetryBatchJobMock;

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

	@Test
	void sellerExtractRetryJob_createsAIndividualSellersRetryBatchJobWithNameIndividualSellersExtractRetryJob() {

		final JobDetail result = testObj.sellerExtractRetryJob(individualSellersRetryBatchJobMock);

		assertThat(result.getJobClass()).hasSameClassAs(QuartzBatchJobBean.class);
		assertThat(result.getKey().getName()).isEqualTo(INDIVIDUAL_SELLERS_EXTRACT_RETRY_JOB_IDENTITY);
		assertThat(result.getJobDataMap()).containsEntry(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN,
				individualSellersRetryBatchJobMock);
	}

	@Test
	void sellerExtractRetryTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = QuartzBatchJobBuilder.newJob(individualSellersRetryBatchJobMock)
				.withIdentity(INDIVIDUAL_SELLERS_EXTRACT_RETRY_JOB_IDENTITY).build();

		final Trigger result = testObj.sellerExtractTrigger(jobDetail, CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(CRON_EXPRESSION);
	}

}
