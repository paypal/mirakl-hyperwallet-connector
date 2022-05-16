package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractBatchJobSupportQuartzJobTest {

	@InjectMocks
	@Spy
	private MyAbstractBatchJobSupportQuartzJob testObj;

	@Mock
	private QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory;

	@Mock
	private BatchJob<BatchJobContext, BatchJobItem<?>> batchJobMock;

	@Mock
	private Job jobMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Test
	void executeBatchJob_shouldWrapWithQuartzJobAndExecute() throws JobExecutionException {
		when(quartzBatchJobAdapterFactory.getQuartzJob(batchJobMock)).thenReturn(jobMock);

		testObj.executeBatchJob(batchJobMock, jobExecutionContextMock);

		verify(jobMock).execute(jobExecutionContextMock);
	}

	static class MyAbstractBatchJobSupportQuartzJob extends AbstractBatchJobSupportQuartzJob {

		protected MyAbstractBatchJobSupportQuartzJob(QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory) {
			super(quartzBatchJobAdapterFactory);
		}

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			// Do Nothing
		}

	}

}