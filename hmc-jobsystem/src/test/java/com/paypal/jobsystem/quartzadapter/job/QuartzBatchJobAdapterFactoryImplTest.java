package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Job;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobAdapterFactoryImplTest {

	@InjectMocks
	private QuartzBatchJobAdapterFactoryImpl testObj;

	@Mock
	private BatchJob<BatchJobContext, BatchJobItem<?>> batchJobMock;

	@Test
	void getQuartzJob_ShouldCreateAdaptedBatchJob() {
		final Job result = testObj.getQuartzJob(batchJobMock);

		assertThat(result).isNotNull().isInstanceOf(QuartzBatchJobAdapter.class);
	}

}
