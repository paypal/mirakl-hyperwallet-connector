package com.paypal.invoices.infraestructure.configuration;

import com.paypal.infrastructure.batchjob.quartz.QuartzBatchJobBean;
import com.paypal.invoices.batchjobs.invoices.InvoicesRetryBatchJob;
import com.paypal.invoices.jobs.InvoicesExtractJob;
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
class InvoicesExtractJobConfigTest {

	private static final String TRIGGER_PREFIX = "Trigger";

	private static final String JOB_NAME = "InvoicesExtractJob";

	private static final String RETRY_JOB_NAME = "InvoicesRetryJob";

	private static final String CRON_EXPRESSION = "0 0 0 1/1 * ? *";

	private static final String RETRY_CRON_EXPRESSION = "0 0/15 * ? * * *";

	@InjectMocks
	private InvoicesExtractJobConfig testObj;

	@Mock
	private InvoicesRetryBatchJob invoicesRetryBatchJob;

	@Test
	void invoicesExtractJob_createsJobDetailWithNameInvoicesJobAndTypeInvoicesExtractJob() {
		final JobDetail result = testObj.invoicesExtractJob();

		assertThat(result.getJobClass()).hasSameClassAs(InvoicesExtractJobConfig.class);
		assertThat(result.getKey().getName()).isEqualTo("InvoicesExtractJob");
	}

	@Test
	void invoicesTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = JobBuilder.newJob(InvoicesExtractJob.class).withIdentity(JOB_NAME).build();

		final Trigger result = testObj.invoicesExtractTrigger(jobDetail, CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREFIX + JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(CRON_EXPRESSION);
	}

	@Test
	void invoicesRetryJob_createsJobDetailWithNameInvoicesRetryJobAndInvoicesRetryBatchJob() {
		final JobDetail result = testObj.invoicesRetryJob(invoicesRetryBatchJob);

		assertThat(result.getJobClass()).hasSameClassAs(QuartzBatchJobBean.class);
		assertThat(result.getKey().getName()).isEqualTo(RETRY_JOB_NAME);
		assertThat(result.getJobDataMap()).containsEntry("batchJob", invoicesRetryBatchJob);
	}

	@Test
	void invoicesRetryJobTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = testObj.invoicesRetryJob(invoicesRetryBatchJob);

		final Trigger result = testObj.invoicesRetryTrigger(jobDetail, RETRY_CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREFIX + RETRY_JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(RETRY_CRON_EXPRESSION);
	}

}
