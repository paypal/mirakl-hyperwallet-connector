package com.paypal.invoices.extractioncommons.controllers;

import com.paypal.jobsystem.quartzintegration.support.AbstractDeltaInfoJob;
import com.paypal.jobsystem.quartzintegration.services.JobService;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.invoices.extractioncommons.jobs.InvoicesExtractJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvoicesExtractJobControllerTest {

	private static final String JOB_NAME = "jobName";

	private static final String INCLUDE_PAID = "includePaid";

	@InjectMocks
	private InvoiceExtractJobController testObj;

	@Mock
	private JobService jobService;

	@Test
	void runJob_shouldCallCreateAndRunSingleExecutionJobAndTestingInvoiceSessionDataAndIncludePaidFalseWithTheTransformedValues()
			throws SchedulerException {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		final Date delta = DateUtil.convertToDate(now, ZoneId.systemDefault());

		testObj.runJob(delta, "jobName", false);

		verify(jobService).createAndRunSingleExecutionJob(JOB_NAME, InvoicesExtractJob.class,
				AbstractDeltaInfoJob.createJobDataMap(delta, Map.of(INCLUDE_PAID, false)), null);
	}

	@Test
	void runJob_shouldCallCreateAndRunSingleExecutionJobAndTestingInvoiceSessionDataAndIncludePaidTrueWithTheTransformedValues()
			throws SchedulerException {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		final Date delta = DateUtil.convertToDate(now, ZoneId.systemDefault());

		testObj.runJob(delta, "jobName", true);

		verify(jobService).createAndRunSingleExecutionJob(JOB_NAME, InvoicesExtractJob.class,
				AbstractDeltaInfoJob.createJobDataMap(delta, Map.of(INCLUDE_PAID, true)), null);
	}

	@Test
	void runJob_shouldCallCreateAndRunSingleExecutionJobAndTestingInvoiceSessionDataWithTheTransformedValues()
			throws SchedulerException {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		final Date delta = DateUtil.convertToDate(now, ZoneId.systemDefault());

		testObj.runJob(delta, "jobName", null);

		verify(jobService).createAndRunSingleExecutionJob(JOB_NAME, InvoicesExtractJob.class,
				AbstractDeltaInfoJob.createJobDataMap(delta, Map.of()), null);
	}

}
