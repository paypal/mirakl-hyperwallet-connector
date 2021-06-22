package com.paypal.reports.controllers;

import com.paypal.infrastructure.service.JobService;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.reports.jobs.ReportsExtractJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportsExtractControllerTest {

	private static final String JOB_NAME = "jobName";

	private static final String FILE_NAME = "fileName";

	@InjectMocks
	private ReportsExtractController testObj;

	@Mock
	private JobService jobServiceMock;

	@Test
	void runJob_shouldCallCreateAndRunSingleExecutionJobAndTestingReportsSessionDataWithTheTransformedValues()
			throws SchedulerException {

		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime startLocalDate = TimeMachine.now();
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 12, 10, 20, 45));
		final LocalDateTime endLocalDate = TimeMachine.now();

		final Date startDate = DateUtil.convertToDate(startLocalDate, ZoneId.systemDefault());
		final Date endDate = DateUtil.convertToDate(endLocalDate, ZoneId.systemDefault());

		testObj.runJob(startDate, endDate, "jobName", "fileName");

		verify(jobServiceMock).createAndRunSingleExecutionJob(JOB_NAME, ReportsExtractJob.class,
				ReportsExtractJob.createJobDataMap(startDate, endDate, FILE_NAME), null);
	}

}
