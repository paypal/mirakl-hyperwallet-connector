package com.paypal.notifications.incoming;

import com.paypal.notifications.failures.NotificationsFailuresJobConfiguration;
import com.paypal.notifications.failures.jobs.NotificationProcessJob;
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
class NotificationsFailuresJobConfigurationTest {

	private static final String CRON_EXPRESSION = "* 0/15 * * * ? *";

	private static final String TRIGGER_PREFIX = "Trigger";

	private static final String JOB_NAME = "NotificationProcessJob";

	@InjectMocks
	private NotificationsFailuresJobConfiguration testObj;

	@Test
	void extractSellersJob_createsJobDetailWithNameExtractProfessionalSellersJobAndTypeExtractProfessionalSellersJob() {
		final JobDetail result = testObj.notificationProcessJob();

		assertThat(result.getJobClass()).hasSameClassAs(NotificationProcessJob.class);
		assertThat(result.getKey().getName()).isEqualTo("NotificationProcessJob");
	}

	@Test
	void sellerExtractTrigger_shouldReturnATriggerCreatedWithTheCronExpressionPassedAsArgumentAndJob() {
		final JobDetail jobDetail = JobBuilder.newJob(NotificationProcessJob.class).withIdentity(JOB_NAME).build();

		final Trigger result = testObj.notificationProcessTrigger(jobDetail, CRON_EXPRESSION);

		assertThat(result.getJobKey()).isEqualTo(jobDetail.getKey());
		assertThat(result.getKey()).isEqualTo(TriggerKey.triggerKey(TRIGGER_PREFIX + JOB_NAME));
		assertThat(result).isInstanceOf(CronTriggerImpl.class);
		assertThat(((CronTriggerImpl) result).getCronExpression()).isEqualTo(CRON_EXPRESSION);
	}

}
