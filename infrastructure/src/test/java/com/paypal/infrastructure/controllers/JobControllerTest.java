package com.paypal.infrastructure.controllers;

import com.paypal.infrastructure.model.entity.JobExecutionInformationEntity;
import com.paypal.infrastructure.service.JobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobControllerTest {

	private static final String JOB_NAME_1 = "job1";

	private static final String JOB_NAME_2 = "job2";

	@InjectMocks
	private JobController testObj;

	@Mock
	private JobService jobServiceMock;

	@Mock
	private JobExecutionInformationEntity jobExecutionInformationEntityOneMock, jobExecutionInformationEntityTwoMock;

	@Test
	void status_shouldReplyWithListOfJobs() throws SchedulerException {
		final JobDetail job1 = JobBuilder.newJob(MyJob.class).withIdentity(JOB_NAME_1).build();
		job1.getJobDataMap().put("runningInstanceId", jobExecutionInformationEntityOneMock);
		final JobDetail job2 = JobBuilder.newJob(MyJob.class).withIdentity(JOB_NAME_2).build();
		job2.getJobDataMap().put("runningInstanceId", jobExecutionInformationEntityTwoMock);
		when(jobServiceMock.getJobs()).thenReturn(Set.of(job1, job2));

		final ResponseEntity<List<JobExecutionInformationEntity>> result = testObj.status();

		assertThat(result.getStatusCode()).isEqualByComparingTo(HttpStatus.ACCEPTED);
		assertThat(result.getBody()).containsExactlyInAnyOrder(jobExecutionInformationEntityOneMock,
				jobExecutionInformationEntityTwoMock);
	}

	static class MyJob implements Job {

		@Override
		public void execute(final JobExecutionContext context) {
			// doNothing
		}

	}

}
