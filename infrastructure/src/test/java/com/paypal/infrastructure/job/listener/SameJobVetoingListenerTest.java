package com.paypal.infrastructure.job.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SameJobVetoingListenerTest {

	@InjectMocks
	private SameJobVetoingListener testObj;

	@Mock
	private Trigger triggerMock;

	@Mock
	private Scheduler schedulerMock;

	@Mock
	private JobExecutionContext jobToBeTriggeredContextMock;

	@Mock
	private JobExecutionContext jobAlreadyExecutingContextMock;

	@Test
	void getName() {
		final String result = testObj.getName();

		assertThat(result).isEqualTo("sameJobVetoingListener");
	}

	@Test
	void vetoJobExecution_shouldReturnFalseWhenNoOtherJobOfSameClassIsRunning() throws SchedulerException {
		final BarJobClass jobToBeTriggered = new BarJobClass();
		when(jobToBeTriggeredContextMock.getJobInstance()).thenReturn(jobToBeTriggered);
		final FooJobClass jobBeingExecuted = new FooJobClass();
		when(jobAlreadyExecutingContextMock.getJobInstance()).thenReturn(jobBeingExecuted);

		when(schedulerMock.getCurrentlyExecutingJobs()).thenReturn(List.of(jobAlreadyExecutingContextMock));

		final boolean result = testObj.vetoJobExecution(triggerMock, jobToBeTriggeredContextMock);

		assertThat(result).isFalse();
	}

	@Test
	void vetoJobExecution_shouldReturnTrueWhenOtherJobOfSameClassIsRunning() throws SchedulerException {
		final FooJobClass jobToBeTriggered = new FooJobClass();
		when(jobToBeTriggeredContextMock.getJobInstance()).thenReturn(jobToBeTriggered);
		when(jobToBeTriggeredContextMock.getJobDetail())
				.thenReturn(JobBuilder.newJob(FooJobClass.class).withIdentity("jobName").build());
		final FooJobClass jobBeingExecuted = new FooJobClass();
		when(jobAlreadyExecutingContextMock.getJobInstance()).thenReturn(jobBeingExecuted);

		when(schedulerMock.getCurrentlyExecutingJobs()).thenReturn(List.of(jobAlreadyExecutingContextMock));

		final boolean result = testObj.vetoJobExecution(triggerMock, jobToBeTriggeredContextMock);

		assertThat(result).isTrue();
	}

	private static class FooJobClass implements Job {

		@Override
		public void execute(final JobExecutionContext context) {
			// doNothing
		}

	}

	private static class BarJobClass implements Job {

		@Override
		public void execute(final JobExecutionContext context) {
			// doNothing
		}

	}

}
