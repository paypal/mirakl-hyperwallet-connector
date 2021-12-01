package com.paypal.infrastructure.controllers;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.infrastructure.service.JobService;
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

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AbstractJobControllerTest {

	@Spy
	@InjectMocks
	private MyAbstractJobController testObj;

	@Mock
	private JobService jobServiceMock;

	@Test
	void runSingleJob_shouldCallJobServiceCreateAndRunSingleExecutionJobWithParamsPassed() throws SchedulerException {
		final Date now = new Date();
		final String jobName = "newJob";
		final JobDataMap jobDataMap = AbstractDeltaInfoJob.createJobDataMap(now);

		testObj.runSingleJob(jobName, MyInfoJob.class, now);

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
