package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobBeanTest {

	@InjectMocks
	private QuartzBatchJobBean testObj;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private TestBatchJob batchJobMock;

	@Mock
	private QuartzBatchJobAdapter quartzBatchJobAdapterMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private JobDataMap jobDataMapMock;

	@Test
	void executeInternal_shouldResolveTheBatchJobBeanByNameAndExecuteIt() throws JobExecutionException {
		testObj.setBatchJob(TestBatchJob.class.getName());
		when(beanFactory.getBean(TestBatchJob.class)).thenReturn(batchJobMock);
		when(quartzBatchJobAdapterFactory.getQuartzJob(batchJobMock)).thenReturn(quartzBatchJobAdapterMock);

		testObj.executeInternal(jobExecutionContextMock);

		verify(quartzBatchJobAdapterMock).execute(jobExecutionContextMock);
	}

	/**
	 * A persisted job can outlive the class that defined it. That must fail this
	 * execution cleanly rather than refire, since retrying cannot bring the class back.
	 */
	@Test
	void executeInternal_shouldFailWithoutRefiring_whenTheBatchJobClassIsGone() {
		testObj.setBatchJob("com.paypal.jobsystem.NoLongerExistingBatchJob");

		assertThatThrownBy(() -> testObj.executeInternal(jobExecutionContextMock))
			.isInstanceOf(JobExecutionException.class)
			.hasMessageContaining("NoLongerExistingBatchJob")
			.matches(e -> !((JobExecutionException) e).refireImmediately(), "does not refire immediately");
	}

	@Test
	void executeInternal_shouldFailWithoutRefiring_whenTheBatchJobBeanIsNotDefined() {
		testObj.setBatchJob(TestBatchJob.class.getName());
		when(beanFactory.getBean(TestBatchJob.class)).thenThrow(new NoSuchBeanDefinitionException(TestBatchJob.class));

		assertThatThrownBy(() -> testObj.executeInternal(jobExecutionContextMock))
			.isInstanceOf(JobExecutionException.class)
			.matches(e -> !((JobExecutionException) e).refireImmediately(), "does not refire immediately");
	}

	@Test
	void getBatchJobClassName_shouldReturnTheNameStoredInTheJobDataMap() {
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMapMock);
		when(jobDataMapMock.get(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN)).thenReturn(TestBatchJob.class.getName());

		assertThat(QuartzBatchJobBean.getBatchJobClassName(jobExecutionContextMock))
			.isEqualTo(TestBatchJob.class.getName());
	}

	/** Stands in for a concrete batch job bean; only its type and name matter here. */
	interface TestBatchJob extends BatchJob<BatchJobContext, BatchJobItem<?>> {

	}

}
