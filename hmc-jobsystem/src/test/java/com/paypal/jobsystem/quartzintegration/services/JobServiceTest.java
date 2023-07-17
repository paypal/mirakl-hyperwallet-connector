package com.paypal.jobsystem.quartzintegration.services;

import com.paypal.jobsystem.quartzintegration.repositories.entities.JobStatus;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;
import org.quartz.core.jmx.JobDataMapSupport;
import org.quartz.impl.matchers.GroupMatcher;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

	private static final String NEW_JOB = "newJob";

	@InjectMocks
	private JobService testObj;

	@Mock
	private Scheduler schedulerMock;

	private final JobDetail runningJobDetail = JobBuilder.newJob(Job.class)
			.setJobData(JobDataMapSupport.newJobDataMap(Map.of("status", JobStatus.RUNNING)))
			.withIdentity("runningJobDetail").build();

	private final JobDetail noStatusJobDetail = JobBuilder.newJob(Job.class).withIdentity("noStatusJobDetail").build();

	@Mock
	private JobKey jobKeyMock;

	@Mock
	private JobDetail jobDetailMock;

	@Captor
	private ArgumentCaptor<JobDetail> jobDetailArgumentCaptor;

	@Captor
	private ArgumentCaptor<Trigger> expectTriggerArgumentCaptor;

	@Test
	void getJobs_shouldReturnAllJobsRegisteredOnSystem() throws SchedulerException {
		when(schedulerMock.getJobKeys(GroupMatcher.anyJobGroup()))
				.thenReturn(Set.of(runningJobDetail.getKey(), noStatusJobDetail.getKey()));

		when(schedulerMock.getJobDetail(runningJobDetail.getKey())).thenReturn(runningJobDetail);
		when(schedulerMock.getJobDetail(noStatusJobDetail.getKey())).thenReturn(noStatusJobDetail);

		final Set<JobDetail> result = testObj.getJobs();

		assertThat(result).containsExactly(runningJobDetail, noStatusJobDetail);
	}

	@Test
	void getJobs_whenExceptionIsThrownByScheduler_shouldNotRetrieveJobs() throws SchedulerException {
		when(schedulerMock.getJobKeys(GroupMatcher.anyJobGroup()))
				.thenReturn(Set.of(runningJobDetail.getKey(), noStatusJobDetail.getKey()));

		when(schedulerMock.getJobDetail(runningJobDetail.getKey())).thenReturn(runningJobDetail);
		doThrow(new SchedulerException()).when(schedulerMock).getJobDetail(noStatusJobDetail.getKey());

		final Set<JobDetail> result = testObj.getJobs();

		assertThat(result).containsExactly(runningJobDetail);
	}

	@Test
	void getJobStatus_whenStatusInsideJobDataMapIsInRunningState_shouldReturnRunning() throws SchedulerException {
		when(schedulerMock.getJobKeys(GroupMatcher.anyJobGroup()))
				.thenReturn(Set.of(runningJobDetail.getKey(), noStatusJobDetail.getKey()));

		when(schedulerMock.getJobDetail(runningJobDetail.getKey())).thenReturn(runningJobDetail);
		when(schedulerMock.getJobDetail(noStatusJobDetail.getKey())).thenReturn(noStatusJobDetail);

		final JobStatus result = testObj.getJobStatus("runningJobDetail");

		assertThat(result).isEqualTo(JobStatus.RUNNING);
	}

	@Test
	void getJobStatus_whenJobDataMapIsNull_shouldReturnUnknown() throws SchedulerException {
		when(schedulerMock.getJobKeys(GroupMatcher.anyJobGroup())).thenReturn(Set.of(jobKeyMock));
		when(schedulerMock.getJobDetail(jobKeyMock)).thenReturn(jobDetailMock);
		when(jobKeyMock.getName()).thenReturn("mockedJob");
		when(jobDetailMock.getKey()).thenReturn(jobKeyMock);

		final JobStatus result = testObj.getJobStatus("mockedJob");

		assertThat(result).isEqualTo(JobStatus.UNKNOWN);
	}

	@Test
	void getJobStatus_whenJobDataMapDoesNotContainStatusKey_shouldReturnUnknown() throws SchedulerException {
		when(schedulerMock.getJobKeys(GroupMatcher.anyJobGroup())).thenReturn(Set.of(jobKeyMock));
		when(schedulerMock.getJobDetail(jobKeyMock)).thenReturn(jobDetailMock);
		when(jobKeyMock.getName()).thenReturn("mockedJob");
		when(jobDetailMock.getKey()).thenReturn(jobKeyMock);
		when(jobDetailMock.getJobDataMap()).thenReturn(new JobDataMap());

		final JobStatus result = testObj.getJobStatus("mockedJob");

		assertThat(result).isEqualTo(JobStatus.UNKNOWN);
	}

	@Test
	void getJobStatus_whenJobIsNotFound_shouldReturnUnknown() throws SchedulerException {
		when(schedulerMock.getJobKeys(GroupMatcher.anyJobGroup()))
				.thenReturn(Set.of(runningJobDetail.getKey(), noStatusJobDetail.getKey()));

		when(schedulerMock.getJobDetail(runningJobDetail.getKey())).thenReturn(runningJobDetail);
		when(schedulerMock.getJobDetail(noStatusJobDetail.getKey())).thenReturn(noStatusJobDetail);

		final JobStatus result = testObj.getJobStatus("nonExisting");

		assertThat(result).isEqualTo(JobStatus.UNKNOWN);
	}

	@Test
	void getJobStatus_whenJobDataMapIsEmpty_shouldReturnUnknown() throws SchedulerException {
		when(schedulerMock.getJobKeys(GroupMatcher.anyJobGroup()))
				.thenReturn(Set.of(runningJobDetail.getKey(), noStatusJobDetail.getKey()));

		when(schedulerMock.getJobDetail(runningJobDetail.getKey())).thenReturn(runningJobDetail);
		when(schedulerMock.getJobDetail(noStatusJobDetail.getKey())).thenReturn(noStatusJobDetail);

		final JobStatus result = testObj.getJobStatus("noStatusJobDetail");

		assertThat(result).isEqualTo(JobStatus.UNKNOWN);
	}

	@Test
	void createAndRunSingleExecutionJob_whenDateIsEmpty_shouldScheduleNewJobToBeExecutedNow()
			throws SchedulerException {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
		testObj.createAndRunSingleExecutionJob(NEW_JOB, TestJobClass.class,
				JobDataMapSupport.newJobDataMap(Map.of("param1Key", "param1")), null);

		//@formatter:off
		final JobDetail expectedJobDetail = JobBuilder.newJob(TestJobClass.class)
				.withIdentity(NEW_JOB)
				.usingJobData(JobDataMapSupport.newJobDataMap(Map.of("param1Key", "param1")))
				.storeDurably()
				.build();
		final Trigger expectedTrigger = TriggerBuilder.newTrigger()
				.forJob(expectedJobDetail)
				.startAt(DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault()))
				.build();
		//@formatter"on

		verify(schedulerMock).addJob(jobDetailArgumentCaptor.capture(), eq(true));
		verify(schedulerMock).scheduleJob(expectTriggerArgumentCaptor.capture());
		assertThat(jobDetailArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedJobDetail);
		final Trigger capturedTrigger = expectTriggerArgumentCaptor.getValue();
		assertThat(capturedTrigger.getStartTime()).isEqualTo(expectedTrigger.getStartTime());
		assertThat(capturedTrigger.getJobKey()).isEqualTo(expectedTrigger.getJobKey());
	}

	@Test
	void createAndRunSingleExecutionJob_shouldScheduleNewJobToBeExecutedInDateProvided() throws SchedulerException {
		final Date schedule = new Date();
		testObj.createAndRunSingleExecutionJob(NEW_JOB, TestJobClass.class,
				JobDataMapSupport.newJobDataMap(Map.of("param1Key", "param1")), schedule);

		//@formatter:off
		final JobDetail expectedJobDetail = JobBuilder.newJob(TestJobClass.class)
				.withIdentity(NEW_JOB)
				.usingJobData(JobDataMapSupport.newJobDataMap(Map.of("param1Key", "param1")))
				.storeDurably()
				.build();
		final Trigger expectedTrigger = TriggerBuilder.newTrigger()
				.forJob(expectedJobDetail)
				.startAt(schedule)
				.build();
		//@formatter"on

		verify(schedulerMock).addJob(jobDetailArgumentCaptor.capture(), eq(true));
		verify(schedulerMock).scheduleJob(expectTriggerArgumentCaptor.capture());
		assertThat(jobDetailArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedJobDetail);
		//@formatter:off
		assertThat(expectTriggerArgumentCaptor.getValue())
				.usingRecursiveComparison()
				.ignoringFields("name", "key.name")
				.ignoringActualNullFields()
				.isEqualTo(expectedTrigger);
		//@formatter:on
	}

	private static class TestJobClass implements Job {

		@Override
		public void execute(final JobExecutionContext context) {
			// doNothing
		}

	}

}
