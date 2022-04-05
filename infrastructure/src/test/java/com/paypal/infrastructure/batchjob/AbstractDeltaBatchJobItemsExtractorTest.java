package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.model.entity.JobExecutionInformationEntity;
import com.paypal.infrastructure.repository.JobExecutionInformationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractDeltaBatchJobItemsExtractorTest {

	private static final String DELTA_KEY = "delta";

	private static final Date DELTA = new Date();

	private AbstractDeltaBatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> testObj;

	@Mock
	private JobExecutionInformationRepository jobExecutionInformationRepositoryMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private JobDataMap jobDataMapMock;

	@BeforeEach
	public void setUp() {
		testObj = new MyAbstractDeltaBatchJobItemsExtractor();
		testObj.setJobExecutionInformationRepository(jobExecutionInformationRepositoryMock);
		when(batchJobContextMock.getJobExecutionContext()).thenReturn(jobExecutionContextMock);
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMapMock);
		when(jobDataMapMock.get(DELTA_KEY)).thenReturn(DELTA);
		lenient().doReturn(MyAbstractDeltaBatchJobItemsExtractor.class).when(jobDetailMock).getJobClass();
	}

	@Test
	void getDelta_ShouldReturnJobDetailDelta_WhenItIsNotNull() {

		final Date result = testObj.getDelta(batchJobContextMock);

		assertThat(result).isEqualTo(DELTA);
	}

	@Test
	void getDelta_ShouldReturnDeltaFromRepository_WhenItIsNull() {

		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA_KEY))
				.thenReturn(null);
		final JobExecutionInformationEntity jobExecutionInformationEntity = new JobExecutionInformationEntity();
		jobExecutionInformationEntity.setStartTime(DELTA);
		when(jobExecutionInformationRepositoryMock.findTopByTypeAndEndTimeIsNotNullOrderByIdDesc(
				MyAbstractDeltaBatchJobItemsExtractor.class.getSimpleName())).thenReturn(jobExecutionInformationEntity);

		final Date result = testObj.getDelta(batchJobContextMock);

		assertThat(result).isEqualTo(DELTA);
	}

	@Test
	void getDelta_ShouldReturnNull_WhenJobExecutionInformationRepositoryReturnsNull() {

		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA_KEY))
				.thenReturn(null);
		when(jobExecutionInformationRepositoryMock.findTopByTypeAndEndTimeIsNotNullOrderByIdDesc(
				MyAbstractDeltaBatchJobItemsExtractor.class.getSimpleName())).thenReturn(null);

		final Date result = testObj.getDelta(batchJobContextMock);

		assertThat(result).isNull();
	}

	@Test
	void getDelta_ShouldReturnNull_WhenJobExecutionStartTimeIsNull() {

		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA_KEY))
				.thenReturn(null);
		when(jobExecutionInformationRepositoryMock.findTopByTypeAndEndTimeIsNotNullOrderByIdDesc(
				MyAbstractDeltaBatchJobItemsExtractor.class.getSimpleName()))
						.thenReturn(new JobExecutionInformationEntity());

		final Date result = testObj.getDelta(batchJobContextMock);

		assertThat(result).isNull();
	}

	private static class MyAbstractDeltaBatchJobItemsExtractor
			extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> {

		@Override
		protected Collection<BatchJobItem<Object>> getItems(final Date delta) {
			return List.of();
		}

	}

}
