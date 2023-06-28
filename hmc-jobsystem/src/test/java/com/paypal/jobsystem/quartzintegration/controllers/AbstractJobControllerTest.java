package com.paypal.jobsystem.quartzintegration.controllers;

import com.paypal.jobsystem.quartzintegration.support.AbstractDeltaInfoJob;
import com.paypal.jobsystem.quartzintegration.services.JobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AbstractJobControllerTest {

	@Spy
	@InjectMocks
	private MyAbstractJobController testObj;

	@Mock
	private JobService jobServiceMock;

	@Test
	void runSingleJob_shouldCallJobServiceCreateAndRunSingleExecutionJobWithThreeParamsPassed()
			throws SchedulerException {
		final Date now = new Date();
		final String jobName = "newJob";
		final JobDataMap jobDataMap = AbstractDeltaInfoJob.createJobDataMap(now);

		testObj.runSingleJob(jobName, MyInfoJob.class, now);

		verify(jobServiceMock).createAndRunSingleExecutionJob(jobName, MyInfoJob.class, jobDataMap, null);
	}

	@Test
	void runSingleJob_shouldCallJobServiceCreateAndRunSingleExecutionJobWithFourParamsPassed()
			throws SchedulerException {
		final Date now = new Date();
		final String jobName = "newJob";
		final JobDataMap jobDataMap = AbstractDeltaInfoJob.createJobDataMap(now, Map.of());

		testObj.runSingleJob(jobName, MyInfoJob.class, now, Map.of());

		verify(jobServiceMock).createAndRunSingleExecutionJob(jobName, MyInfoJob.class, jobDataMap, null);
	}

	private static class MyAbstractJobController extends AbstractJobController {

	}

	private static class MyInfoJob extends AbstractDeltaInfoJob {

		@Override
		public void execute(final JobExecutionContext context) {

		}

	}

}
