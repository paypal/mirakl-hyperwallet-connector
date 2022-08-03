package com.paypal.notifications.jobs;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.notifications.service.FailedNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

/**
 * Extracts stored failed notifications and tries to process them again
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NotificationProcessJob extends AbstractDeltaInfoJob {

	@Value("#{'${notifications.retry}'}")
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
