package com.paypal.infrastructure.configuration;

import com.paypal.infrastructure.job.listener.JobExecutionInformationListener;
import com.paypal.infrastructure.job.listener.SameJobVetoingListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobListenerConfigTest {

	@InjectMocks
	private JobListenerConfig testObj;

	@Mock
	private Scheduler scheduler;

	@Mock
	private ListenerManager listenerManagerMock;

	@Mock
	private JobExecutionInformationListener jobExecutionInformationListenerMock;

	@Mock
	private SameJobVetoingListener sameJobVetoingListener;

	@Captor
	private ArgumentCaptor<JobExecutionInformationListener> jobExecutionInformationListenerArgumentCaptor;

	@Captor
	private ArgumentCaptor<SameJobVetoingListener> SameJobVetoingListenerArgumentCapture;

	@Test
	void jobDeltaListenerInit_shouldAddJobExecutionInformationAndSameJobVetoingListener() throws SchedulerException {
		when(scheduler.getListenerManager()).thenReturn(listenerManagerMock);
		doNothing().when(listenerManagerMock).addJobListener(jobExecutionInformationListenerMock);

		testObj.jobJobExecutionInformationListenerInit();

		verify(listenerManagerMock).addJobListener(jobExecutionInformationListenerArgumentCaptor.capture());

		assertThat(jobExecutionInformationListenerArgumentCaptor.getValue())
				.isEqualTo(jobExecutionInformationListenerMock);
	}

	@Test
	void triggerSameJobVetoingListener_shouldAddJobExecutionInformationAndSameJobVetoingListener()
			throws SchedulerException {
		when(scheduler.getListenerManager()).thenReturn(listenerManagerMock);
		doNothing().when(listenerManagerMock).addTriggerListener(sameJobVetoingListener);

		testObj.triggerSameJobVetoingListener();

		verify(listenerManagerMock).addTriggerListener(SameJobVetoingListenerArgumentCapture.capture());

		assertThat(SameJobVetoingListenerArgumentCapture.getValue()).isEqualTo(sameJobVetoingListener);
	}

}
