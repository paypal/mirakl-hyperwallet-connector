package com.paypal.jobsystem.quartzadapter.jobcontext;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.quartzadapter.jobcontext.QuartzBatchJobContextAdapter;
import com.paypal.jobsystem.quartzadapter.jobcontext.QuartzBatchJobContextFactoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobContextFactoryImplTest {

	@InjectMocks
	@Spy
	private QuartzBatchJobContextFactoryImpl testObj;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private JobDataMap jobDataMapMock;

	@Mock
	private BatchJob batchJobMock;

	@Test
	void getBatchJobContext_ShouldCreateBatchJobContext_ForScheduledJobs() {
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMapMock);
		when(jobDetailMock.getKey()).thenReturn(new JobKey("JobName"));

		final BatchJobContext result = testObj.getBatchJobContext(batchJobMock, jobExecutionContextMock);

		assertThat(result).isNotNull();
		verify(jobDataMapMock, times(1)).put(eq(QuartzBatchJobContextAdapter.KEY_BATCH_JOB_EXECUTION_UUID), any());
		verify(jobDataMapMock, times(1)).put(QuartzBatchJobContextAdapter.KEY_BATCH_JOB, batchJobMock);
		verify(jobDataMapMock, times(1)).put(eq(QuartzBatchJobContextAdapter.KEY_BATCH_JOB_NAME),
				argThat(x -> x.contains(batchJobMock.getClass().getSimpleName()) && !x.contains("MANUAL")));

	}

	@Test
	void getBatchJobContext_ShouldCreateBatchJobContext_ForManualJobs() {
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMapMock);
		when(jobDetailMock.getKey()).thenReturn(new JobKey("JobName_1234"));

		final BatchJobContext result = testObj.getBatchJobContext(batchJobMock, jobExecutionContextMock);

		assertThat(result).isNotNull();
		verify(jobDataMapMock, times(1)).put(eq(QuartzBatchJobContextAdapter.KEY_BATCH_JOB_EXECUTION_UUID), any());
		verify(jobDataMapMock, times(1)).put(QuartzBatchJobContextAdapter.KEY_BATCH_JOB, batchJobMock);
		verify(jobDataMapMock, times(1)).put(eq(QuartzBatchJobContextAdapter.KEY_BATCH_JOB_NAME),
				argThat(x -> x.contains(batchJobMock.getClass().getSimpleName()) && x.contains("#MANUAL#1234")));

	}

}
