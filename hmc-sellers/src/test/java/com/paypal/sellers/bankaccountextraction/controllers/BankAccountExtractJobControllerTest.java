package com.paypal.sellers.bankaccountextraction.controllers;

import com.paypal.jobsystem.quartzintegration.support.AbstractDeltaInfoJob;
import com.paypal.jobsystem.quartzintegration.services.JobService;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.sellers.bankaccountextraction.controllers.BankAccountExtractJobController;
import com.paypal.sellers.bankaccountextraction.jobs.BankAccountExtractJob;
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
class BankAccountExtractJobControllerTest {

	private static final String JOB_NAME = "jobName";

	@InjectMocks
	private BankAccountExtractJobController testObj;

	@Mock
	private JobService jobService;

	@Test
	void runJob_shouldCallJobServiceWithValuesPassedAsParam() throws SchedulerException {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		final Date delta = DateUtil.convertToDate(now, ZoneId.systemDefault());

		testObj.runJob(delta, "jobName");

		verify(jobService).createAndRunSingleExecutionJob(JOB_NAME, BankAccountExtractJob.class,
				AbstractDeltaInfoJob.createJobDataMap(delta), null);
	}

}
