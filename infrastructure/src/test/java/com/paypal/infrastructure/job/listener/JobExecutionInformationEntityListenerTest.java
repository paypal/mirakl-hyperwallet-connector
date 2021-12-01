package com.paypal.infrastructure.job.listener;

import com.paypal.infrastructure.model.entity.JobExecutionInformationEntity;
import com.paypal.infrastructure.model.job.JobStatus;
import com.paypal.infrastructure.repository.JobExecutionInformationRepository;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobExecutionInformationEntityListenerTest {

	private static final String MY_JOB_NAME_EXECUTION = "myJobNameExecution";

	@InjectMocks
	private JobExecutionInformationListener testObj;

	@Mock
	private Scheduler schedulerMock;

	@Mock
	private JobExecutionContext contextMock;

	@Mock
	private JobExecutionInformationRepository jobExecutionInformationRepositoryMock;

	@Mock
	private JobExecutionInformationEntity savedJobExecutionInformationEntityMock;

	@Mock
	private JobExecutionException jobExceptionMock;

	@Captor
	private ArgumentCaptor<JobExecutionInformationEntity> jobExecutionInformationEntityArgumentCaptor;

	@Test
	void jobToBeExecuted_shouldSaveTheJobExecutionInformationOfLastExecution() {
		final LocalDateTime now = LocalDateTime.of(2020, 11, 15, 22, 20);
		TimeMachine.useFixedClockAt(now);
		final JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity(MY_JOB_NAME_EXECUTION).build();
		when(contextMock.getJobDetail()).thenReturn(jobDetail);
		when(jobExecutionInformationRepositoryMock.save(any(JobExecutionInformationEntity.class)))
				.thenReturn(savedJobExecutionInformationEntityMock);

		testObj.jobToBeExecuted(contextMock);

		verify(jobExecutionInformationRepositoryMock).save(jobExecutionInformationEntityArgumentCaptor.capture());
		//@formatter:off
		assertThat(jobExecutionInformationEntityArgumentCaptor.getValue()).hasFieldOrPropertyWithValue("type", "MyJob")
				.hasFieldOrPropertyWithValue("name", MY_JOB_NAME_EXECUTION)
				.hasFieldOrPropertyWithValue("startTime", DateUtil.convertToDate(now, ZoneId.systemDefault()))
				.hasFieldOrPropertyWithValue("status", JobStatus.RUNNING);
		//@formatter:on
	}

	@Test
	void jobWasExecuted_shouldSaveTheJobExecutionInformationOfFinalisedLastExecution() {
		final LocalDateTime now = LocalDateTime.of(2020, 11, 15, 22, 20);
		TimeMachine.useFixedClockAt(now);
		final JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity(MY_JOB_NAME_EXECUTION).build();
		jobDetail.getJobDataMap().put("runningInstanceId", savedJobExecutionInformationEntityMock);
		when(contextMock.getJobDetail()).thenReturn(jobDetail);

		testObj.jobWasExecuted(contextMock, jobExceptionMock);

		verify(savedJobExecutionInformationEntityMock).setStatus(JobStatus.COMPLETED);
		verify(savedJobExecutionInformationEntityMock).setEndTime(DateUtil.convertToDate(now, ZoneId.systemDefault()));
		verify(jobExecutionInformationRepositoryMock).save(savedJobExecutionInformationEntityMock);
	}

	private static class MyJob implements Job {

		@Override
		public void execute(final JobExecutionContext context) {
			// doNothing
		}

	}

}
