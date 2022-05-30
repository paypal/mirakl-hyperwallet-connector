package com.paypal.invoices.infraestructure.configuration;

import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBean;
import com.paypal.invoices.batchjobs.creditnotes.CreditNotesRetryBatchJob;
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
class CreditNotesExtractJobConfigTest {

	@InjectMocks
	private CreditNotesExtractJobConfig testObj;

	private static final String RETRY_CRON_EXPRESSION = "0 0/15 * ? * * *";

	private static final String TRIGGER_PREFIX = "Trigger";

	private static final String RETRY_JOB_NAME = "CreditNotesRetryJob";

	@Mock
	private CreditNotesRetryBatchJob creditNotesRetryBatchJob;

	@Test
	void creditNotesRetryJob_createsJobDetailWithNameCreditNotesRetryJobAndCreditNotesRetryBatchJob() {
		final JobDetail result = testObj.creditNotesRetryJob(creditNotesRetryBatchJob);

		assertThat(result.getJobClass()).hasSameClassAs(QuartzBatchJobBean.class);
		assertThat(result.getKey().getName()).isEqualTo(RETRY_JOB_NAME);
		assertThat(result.getJobDataMap()).containsEntry("batchJob", creditNotesRetryBatchJob);
	}

	@Test
	void bankAccountRetryJobTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = testObj.creditNotesRetryJob(creditNotesRetryBatchJob);

		final Trigger result = testObj.creditNotesRetryTrigger(jobDetail, RETRY_CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREFIX + RETRY_JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(RETRY_CRON_EXPRESSION);
	}

}
