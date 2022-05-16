package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobBuilderTest {

	private QuartzBatchJobBuilder testObj;

	@Mock
	BatchJob batchJobMock;

	@Test
	void shouldBuildQuartzBatchJob() {
		JobDetail result = QuartzBatchJobBuilder.newJob(batchJobMock).build();

		assertThat(result.getJobClass()).isEqualTo(QuartzBatchJobBean.class);
		assertThat(result.getJobDataMap()).containsEntry(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN, batchJobMock);
	}

}