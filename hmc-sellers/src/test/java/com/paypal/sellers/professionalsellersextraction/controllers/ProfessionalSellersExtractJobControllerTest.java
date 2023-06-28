package com.paypal.sellers.professionalsellersextraction.controllers;

import com.paypal.jobsystem.quartzintegration.support.AbstractDeltaInfoJob;
import com.paypal.jobsystem.quartzintegration.services.JobService;
import com.paypal.sellers.professionalsellersextraction.jobs.ProfessionalSellersExtractJob;
import com.paypal.sellers.professionalsellersextraction.controllers.ProfessionalSellersExtractJobController;
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
		this.testObj.runJob(this.deltaMock, "jobName");

		verify(this.jobService).createAndRunSingleExecutionJob(JOB_NAME, ProfessionalSellersExtractJob.class,
				AbstractDeltaInfoJob.createJobDataMap(this.deltaMock), null);
	}

}
