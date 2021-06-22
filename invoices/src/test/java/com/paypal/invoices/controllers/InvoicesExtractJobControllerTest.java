package com.paypal.invoices.controllers;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.infrastructure.service.JobService;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.invoices.jobs.InvoicesExtractJob;
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
class InvoicesExtractJobControllerTest {

	private static final String JOB_NAME = "jobName";

	@InjectMocks
	private InvoiceExtractJobController testObj;

	@Mock
	private JobService jobService;

	@Test
	void runJob_shouldCallCreateAndRunSingleExecutionJobAndTestingInvoiceSessionDataWithTheTransformedValues()
			throws SchedulerException {

		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		final Date delta = DateUtil.convertToDate(now, ZoneId.systemDefault());

		testObj.runJob(delta, "jobName");

		verify(jobService).createAndRunSingleExecutionJob(JOB_NAME, InvoicesExtractJob.class,
				AbstractDeltaInfoJob.createJobDataMap(delta), null);
	}

}
