package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuartzBatchJobContextFactoryImplTest {

	@InjectMocks
	private QuartzBatchJobContextFactoryImpl testObj;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private JobDataMap jobDataMapMock;

	@Test
	void getBatchJobContext_ShouldCreateBatchJobContext() {
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMapMock);
		BatchJobContext result = testObj.getBatchJobContext(jobExecutionContextMock);

		assertThat(result).isNotNull();
	}

}