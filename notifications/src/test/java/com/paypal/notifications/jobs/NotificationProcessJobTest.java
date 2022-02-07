package com.paypal.notifications.jobs;

import com.paypal.notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationProcessJobTest {

	@Spy
	@InjectMocks
	private NotificationProcessJob testObj;

	@Mock
	protected NotificationService notificationServiceMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Test
	void execute_whenRetryNotificationsAreEnabled_shouldProcessFailedNotifications() {
		doReturn(true).when(testObj).shouldRetryNotifications();

		testObj.execute(jobExecutionContextMock);

		verify(notificationServiceMock).processFailedNotifications();
	}

	@Test
	void execute_whenRetryNotificationsAreDisabled_shouldNotProcessFailedNotifications() {
		doReturn(false).when(testObj).shouldRetryNotifications();

		testObj.execute(jobExecutionContextMock);

		verify(notificationServiceMock, never()).processFailedNotifications();
	}

}
