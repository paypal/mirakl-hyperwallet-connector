package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapter;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobBeanTest {

	@InjectMocks
	private QuartzBatchJobBean testObj;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory;

	@Mock
	private BatchJob<BatchJobContext, BatchJobItem<?>> batchJobMock;

	@Mock
	private QuartzBatchJobAdapter quartzBatchJobAdapterMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private JobDataMap jobDataMapMock;

	@Test
	void executeInternal_shouldExecuteBatchJob() throws JobExecutionException {
		when(quartzBatchJobAdapterFactory.getQuartzJob(batchJobMock)).thenReturn(quartzBatchJobAdapterMock);

		testObj.executeInternal(jobExecutionContextMock);

		verify(quartzBatchJobAdapterMock).execute(jobExecutionContextMock);
	}

	@Test
	void getBatchJobClass_shouldReturnJobDataMapBatchJobBeanClass() {
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMapMock);
		when(jobDataMapMock.get(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN)).thenReturn(batchJobMock);
		assertThat(QuartzBatchJobBean.getBatchJobClass(jobExecutionContextMock)).isEqualTo(batchJobMock.getClass());
	}

}
