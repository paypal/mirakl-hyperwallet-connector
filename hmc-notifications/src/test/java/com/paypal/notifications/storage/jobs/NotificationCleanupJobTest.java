package com.paypal.notifications.storage.jobs;

import static org.mockito.Mockito.verify;

import com.paypal.notifications.storage.services.NotificationCleanupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

@ExtendWith(MockitoExtension.class)
class NotificationCleanupJobTest {

	@InjectMocks
	private NotificationCleanupJob testObj;

	@Mock
	private NotificationCleanupService notificationCleanupServiceMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Test
	void execute_shouldDelegateToCleanupService() {
		testObj.execute(jobExecutionContextMock);

		verify(notificationCleanupServiceMock).deleteExpiredNotifications();
	}

}
