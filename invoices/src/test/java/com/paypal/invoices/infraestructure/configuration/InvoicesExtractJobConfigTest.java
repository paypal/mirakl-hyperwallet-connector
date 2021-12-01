package com.paypal.invoices.infraestructure.configuration;

import com.paypal.invoices.jobs.InvoicesExtractJob;
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
class InvoicesExtractJobConfigTest {

	private static final String TRIGGER_PREFIX = "Trigger";

	private static final String JOB_NAME = "InvoicesExtractJob";

	private static final String CRON_EXPRESSION = "0 0 0 1/1 * ? *";

	@InjectMocks
	private InvoicesExtractJobConfig testObj;

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

}
