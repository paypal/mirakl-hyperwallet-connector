package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBean;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBuilder;
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
		final JobDetail result = QuartzBatchJobBuilder.newJob(batchJobMock).build();

		assertThat(result.getJobClass()).isEqualTo(QuartzBatchJobBean.class);
		assertThat(result.getJobDataMap()).containsEntry(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN, batchJobMock);
	}

}
