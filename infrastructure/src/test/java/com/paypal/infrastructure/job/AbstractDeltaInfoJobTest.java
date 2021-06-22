package com.paypal.infrastructure.job;

import com.paypal.infrastructure.model.entity.JobExecutionInformationEntity;
import com.paypal.infrastructure.repository.JobExecutionInformationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractDeltaInfoJobTest {

	@InjectMocks
	private MyAbstractDeltaInfoJob testObj;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private JobExecutionInformationRepository jobExecutionInformationRepository;

	@Mock
	private JobExecutionInformationEntity jobExecutionInformationEntityMock;

	@Test
	void createJobDataMap_shouldAddDeltaKeyWithDatePassedAsArgument() {
		final Date now = new Date();

		final JobDataMap result = MyAbstractDeltaInfoJob.createJobDataMap(now);

		assertThat(result).contains(Map.entry("delta", now));
	}

	@Test
	void getDelta_shouldReturnDeltaTimeWhenJobExecutionContextWithDeltaIsPassedAsArgument() {
		final Date now = new Date();
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("delta", now);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMap);

		final Date result = testObj.getDelta(jobExecutionContextMock);

		assertThat(result).isEqualTo(now);
	}

	@Test
	void getDelta_shouldReturnDeltaTimeWhenJobExecutionContextWithDeltaIsNotPassedAsAndJobWasPreviouslyRunArgument() {
		final Date now = new Date();
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		final JobDataMap jobDataMap = new JobDataMap();
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMap);
		doReturn(MyJob.class).when(jobDetailMock).getJobClass();
		when(jobExecutionInformationRepository.findTopByTypeAndEndTimeIsNotNullOrderByIdDesc(any()))
				.thenReturn(jobExecutionInformationEntityMock);
		when(jobExecutionInformationEntityMock.getStartTime()).thenReturn(now);

		final Date result = testObj.getDelta(jobExecutionContextMock);

		assertThat(result).isEqualTo(now);
	}

	@Test
	void getDelta_shouldReturnNullWhenJobExecutionContextWithDeltaIsNotPassedAsAndJobWasNotPreviouslyRunArgument() {
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		final JobDataMap jobDataMap = new JobDataMap();
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMap);
		doReturn(MyJob.class).when(jobDetailMock).getJobClass();
		when(jobExecutionInformationRepository.findTopByTypeAndEndTimeIsNotNullOrderByIdDesc(any()))
				.thenReturn(jobExecutionInformationEntityMock);
		when(jobExecutionInformationEntityMock.getStartTime()).thenReturn(null);

		final Date result = testObj.getDelta(jobExecutionContextMock);

		assertThat(result).isNull();
	}

	private static class MyAbstractDeltaInfoJob extends AbstractDeltaInfoJob {

		@Override
		public void execute(final JobExecutionContext context) throws JobExecutionException {

		}

	}

	protected static class MyJob implements Job {

		@Override
		public void execute(final JobExecutionContext context) throws JobExecutionException {

		}

	}

}