package com.paypal.jobsystem.quartzintegration.support;

import com.paypal.jobsystem.quartzintegration.repositories.entities.JobExecutionInformationEntity;
import com.paypal.jobsystem.quartzintegration.repositories.JobExecutionInformationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractDeltaInfoJobTest {

	private static final String INCLUDE_PAID = "includePaid";

	private static final String DELTA = "delta";

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

		assertThat(result).contains(Map.entry(DELTA, now));
	}

	@Test
	void createJobDataMap_shouldAddDeltaKeyWithDateAndIncludePaidKeyWithBooleanPassedAsArguments() {
		final Date now = new Date();

		final JobDataMap result = MyAbstractDeltaInfoJob.createJobDataMap(now, Map.of(INCLUDE_PAID, true));

		assertThat(result).contains(Map.entry(DELTA, now));
		assertThat(result).contains(Map.entry(INCLUDE_PAID, true));
	}

	@Test
	void getDelta_shouldReturnDeltaTimeWhenJobExecutionContextWithDeltaIsPassedAsArgument() {
		final Date now = new Date();
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(DELTA, now);
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
		public void execute(final JobExecutionContext context) {

		}

	}

	protected static class MyJob implements Job {

		@Override
		public void execute(final JobExecutionContext context) {

		}

	}

}
