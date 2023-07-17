package com.paypal.notifications.failures.jobs;

import com.paypal.jobsystem.quartzintegration.support.AbstractDeltaInfoJob;
import com.paypal.notifications.failures.services.FailedNotificationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Value;

/**
 * Extracts stored failed notifications and tries to process them again
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NotificationProcessJob extends AbstractDeltaInfoJob {

	@Value("#{'${hmc.webhooks.retries.enabled}'}")
	private boolean retryNotifications;

	@Resource
	protected FailedNotificationService failedNotificationService;

	@Override
	public void execute(final JobExecutionContext context) {
		if (shouldRetryNotifications()) {
			failedNotificationService.processFailedNotifications();
		}
	}

	protected boolean shouldRetryNotifications() {
		return retryNotifications;
	}

}
