package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobExecutor;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobAdapterTest {

	@InjectMocks
	private QuartzBatchJobAdapter testObj;

	@Mock
	private BatchJobExecutor batchJobExecutorMock;

	@Mock
	private BatchJob<BatchJobContext, BatchJobItem<?>> batchJobMock;

	@Mock
	private QuartzBatchJobContextFactory quartzBatchJobContextFactoryMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void execute_ShouldExecuteAdaptedJob() throws JobExecutionException {
		when(quartzBatchJobContextFactoryMock.getBatchJobContext(batchJobMock, jobExecutionContextMock))
				.thenReturn(batchJobContextMock);
		testObj.execute(jobExecutionContextMock);

		verify(batchJobExecutorMock).execute(eq(batchJobMock), any(BatchJobContext.class));
	}

}
