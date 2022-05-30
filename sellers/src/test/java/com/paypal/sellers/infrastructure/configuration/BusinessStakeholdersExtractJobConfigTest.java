package com.paypal.sellers.infrastructure.configuration;

import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBean;
import com.paypal.sellers.batchjobs.bstk.BusinessStakeholdersRetryBatchJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholdersExtractJobConfigTest {

	private static final String RETRY_CRON_EXPRESSION = "0 0/15 * ? * * *";

	private static final String TRIGGER_PREFIX = "Trigger";

	private static final String RETRY_JOB_NAME = "BusinessStakeholdersRetryJob";

	@InjectMocks
	private BusinessStakeholdersExtractJobConfig testObj;

	@Mock
	private BusinessStakeholdersRetryBatchJob businessStakeholdersRetryBatchJob;

	@Test
	void businessStakeholdersRetryJob_createsJobDetailWithNameBusinessStakeholderRetryJobAndBusinessStakeholdersRetryBatchJob() {
		final JobDetail result = testObj.businessStakeholdersRetryJob(businessStakeholdersRetryBatchJob);

		assertThat(result.getJobClass()).hasSameClassAs(QuartzBatchJobBean.class);
		assertThat(result.getKey().getName()).isEqualTo(RETRY_JOB_NAME);
		assertThat(result.getJobDataMap()).containsEntry("batchJob", businessStakeholdersRetryBatchJob);
	}

	@Test
	void businessStakeholdersRetryJob_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = testObj.businessStakeholdersRetryJob(businessStakeholdersRetryBatchJob);

		final Trigger result = testObj.businessStakeholdersRetryTrigger(jobDetail, RETRY_CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREFIX + RETRY_JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(RETRY_CRON_EXPRESSION);
	}

}
