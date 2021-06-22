package com.paypal.sellers.controllers;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.infrastructure.service.JobService;
import com.paypal.sellers.jobs.ProfessionalSellersExtractJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;

import java.util.Date;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellersExtractJobControllerTest {

	private static final String JOB_NAME = "jobName";

	@InjectMocks
	private ProfessionalSellersExtractJobController testObj;

	@Mock
	private JobService jobService;

	@Mock
	private Date deltaMock;

	@Test
	void runJob_shouldCallJobServiceWithValuesPassedAsParam() throws SchedulerException {
		testObj.runJob(deltaMock, "jobName");

		verify(jobService).createAndRunSingleExecutionJob(JOB_NAME, ProfessionalSellersExtractJob.class,
				AbstractDeltaInfoJob.createJobDataMap(deltaMock), null);
	}

}
