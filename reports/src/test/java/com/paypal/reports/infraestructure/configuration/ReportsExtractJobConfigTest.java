package com.paypal.invoices.infraestructure.configuration;

import com.paypal.reports.infraestructure.configuration.ReportsExtractJobConfig;
import com.paypal.reports.jobs.ReportsExtractJob;
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
class ReportsExtractJobConfigTest {

	private static final String CRON_EXPRESSION = "0 0 0 1/1 * ? *";

	private static final String TRIGGER_PREXIX = "Trigger";

	private static final String JOB_NAME = "ReportsExtractJob";

	@InjectMocks
	private ReportsExtractJobConfig testObj;

	@Test
	void reportsExtractJob_createsJobDetailWithNameReportsJobAndTypeReportsExtractJob() {
		final JobDetail result = testObj.reportsExtractJob();

		assertThat(result.getJobClass()).hasSameClassAs(ReportsExtractJob.class);
		assertThat(result.getKey().getName()).isEqualTo("ReportsExtractJob");
	}

	@Test
	void reportsExtractTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = JobBuilder.newJob(ReportsExtractJob.class).withIdentity(JOB_NAME).build();

		final Trigger result = testObj.reportsExtractTrigger(jobDetail, CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREXIX + JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(CRON_EXPRESSION);
	}

}
