package com.paypal.sellers.infrastructure.configuration;

import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBean;
import com.paypal.sellers.batchjobs.bankaccount.BankAccountRetryBatchJob;
import com.paypal.sellers.jobs.BankAccountExtractJob;
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
class BankAccountExtractJobConfigTest {

	private static final String CRON_EXPRESSION = "0 0 0 1/1 * ? *";

	private static final String RETRY_CRON_EXPRESSION = "0 0/15 * ? * * *";

	private static final String TRIGGER_PREFIX = "Trigger";

	private static final String JOB_NAME = "BankAccountExtractJob";

	private static final String RETRY_JOB_NAME = "BankAccountRetryJob";

	@InjectMocks
	private BankAccountExtractJobConfig testObj;

	@Mock
	private BankAccountRetryBatchJob bankAccountRetryBatchJob;

	@Test
	void bankAccountExtractJob_createsJobDetailWithNameBankAccountJobAndTypeBankAccountExtractJob() {
		final JobDetail result = testObj.bankAccountExtractJob();

		assertThat(result.getJobClass()).hasSameClassAs(BankAccountExtractJobConfig.class);
		assertThat(result.getKey().getName()).isEqualTo("BankAccountExtractJob");
	}

	@Test
	void bankAccountExtractJobTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = JobBuilder.newJob(BankAccountExtractJob.class).withIdentity(JOB_NAME).build();

		final Trigger result = testObj.bankAccountExtractJobTrigger(jobDetail, CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREFIX + JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(CRON_EXPRESSION);
	}

	@Test
	void bankAccountRetryJob_createsJobDetailWithNameBankAccountJobAndBankAccountRetryBatchJob() {
		final JobDetail result = testObj.bankAccountRetryJob(bankAccountRetryBatchJob);

		assertThat(result.getJobClass()).hasSameClassAs(QuartzBatchJobBean.class);
		assertThat(result.getKey().getName()).isEqualTo(RETRY_JOB_NAME);
		assertThat(result.getJobDataMap()).containsEntry("batchJob", bankAccountRetryBatchJob);
	}

	@Test
	void bankAccountRetryJobTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = testObj.bankAccountRetryJob(bankAccountRetryBatchJob);

		final Trigger result = testObj.bankAccountRetryJobTrigger(jobDetail, RETRY_CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREFIX + RETRY_JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(RETRY_CRON_EXPRESSION);
	}

}
