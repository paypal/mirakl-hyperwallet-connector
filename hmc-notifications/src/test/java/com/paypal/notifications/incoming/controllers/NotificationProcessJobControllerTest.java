package com.paypal.notifications.incoming.controllers;

import com.paypal.jobsystem.quartzintegration.support.AbstractDeltaInfoJob;
import com.paypal.jobsystem.quartzintegration.services.JobService;
import com.paypal.notifications.failures.controllers.NotificationProcessJobController;
import com.paypal.notifications.failures.jobs.NotificationProcessJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationProcessJobControllerTest {

	private static final String JOB_NAME = "jobName";

	@InjectMocks
	private NotificationProcessJobController testObj;

	@Mock
	private JobService jobServiceMock;

	@Test
	void runJob_shouldCallJobServiceWithValuesPassedAsParam() throws SchedulerException {
		testObj.runJob(JOB_NAME);

		verify(jobServiceMock).createAndRunSingleExecutionJob(JOB_NAME, NotificationProcessJob.class,
				AbstractDeltaInfoJob.createJobDataMap(null), null);
	}

}
