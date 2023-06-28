package com.paypal.sellers.professionalsellersextraction;

import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBean;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersRetryBatchJob;
import com.paypal.sellers.professionalsellersextraction.jobs.ProfessionalSellersExtractJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellersExtractionJobsConfigTest {

	private static final String CRON_EXPRESSION = "0 0 0 1/1 * ? *";

	private static final String RETRY_CRON_EXPRESSION = "0 0/15 * ? * * *";

	private static final String TRIGGER_PREFIX = "Trigger";

	private static final String JOB_NAME = "ProfessionalSellersExtractJob";

	private static final String RETRY_JOB_NAME = "ProfessionalSellersRetryJob";

	@InjectMocks
	private ProfessionalSellersExtractionJobsConfig testObj;

	@Mock
	private ProfessionalSellersRetryBatchJob professionalSellersRetryBatchJob;

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

	@Test
	void extractSellersRetryJob_createsJobDetailWithNameRetryProfessionalSellersJobAndProfessionalSellersRetryBatchJob() {
		final JobDetail result = testObj.professionalSellerRetryJob(professionalSellersRetryBatchJob);

		assertThat(result.getJobClass()).hasSameClassAs(QuartzBatchJobBean.class);
		assertThat(result.getKey().getName()).isEqualTo(RETRY_JOB_NAME);
		assertThat(result.getJobDataMap()).containsEntry("batchJob", professionalSellersRetryBatchJob);
	}

	@Test
	void sellerRetryTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = testObj.professionalSellerRetryJob(professionalSellersRetryBatchJob);

		final Trigger result = testObj.professionalSellerRetryTrigger(jobDetail, RETRY_CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREFIX + RETRY_JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(RETRY_CRON_EXPRESSION);
	}

}
