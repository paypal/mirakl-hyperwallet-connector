package com.paypal.sellers.infrastructure.configuration;

import com.paypal.sellers.jobs.BankAccountExtractJob;
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
class BankAccountExtractJobConfigTest {

	private static final String CRON_EXPRESSION = "0 0 0 1/1 * ? *";

	private static final String TRIGGER_PREXIX = "Trigger";

	private static final String JOB_NAME = "BankAccountExtractJob";

	@InjectMocks
	private BankAccountExtractJobConfig testObj;

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
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREXIX + JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(CRON_EXPRESSION);
	}

}
