package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobExecutor;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.quartz.Job;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobAdapterFactoryImplTest {

	@InjectMocks
	private QuartzBatchJobAdapterFactoryImpl testObj;

	@Mock
	private BatchJobExecutor batchJobExecutor;

	@Mock
	private QuartzBatchJobContextFactory quartzBatchJobContextFactory;

	@Mock
	private BatchJob<BatchJobContext, BatchJobItem<?>> batchJobMock;

	@Test
	void getQuartzJob_ShouldCreateAdaptedBatchJob() {
		Job result = testObj.getQuartzJob(batchJobMock);

		assertThat(result).isNotNull().isInstanceOf(QuartzBatchJobAdapter.class);
	}

}