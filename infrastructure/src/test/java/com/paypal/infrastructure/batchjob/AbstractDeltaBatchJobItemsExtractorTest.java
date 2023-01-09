package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.entities.BatchJobTrackInfoEntity;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractDeltaBatchJobItemsExtractorTest {

	private static final String DELTA_KEY = "delta";

	private static final Date DELTA = new Date();

	public static final int EXTRACTION_MAX_DAYS = 30;

	private AbstractDeltaBatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> testObj;

	@Mock
	private BatchJobTrackingService batchJobTrackingServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private JobDataMap jobDataMapMock;

	@Mock
	private BatchJobTrackInfoEntity batchJobTrackInfoEntityMock;

	@Captor
	private ArgumentCaptor<LocalDateTime> searchJobFromArgumentCaptor;

	@BeforeEach
	public void setUp() {
		testObj = new MyAbstractDeltaBatchJobItemsExtractor(batchJobTrackingServiceMock);
		when(batchJobContextMock.getJobExecutionContext()).thenReturn(jobExecutionContextMock);
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMapMock);
		when(jobDataMapMock.get(DELTA_KEY)).thenReturn(DELTA);
		lenient().doReturn(MyAbstractDeltaBatchJobItemsExtractor.class).when(jobDetailMock).getJobClass();
		TimeMachine.useSystemDefaultZoneClock();
	}

	@Test
	void getDelta_ShouldReturnJobDetailDelta_WhenItIsNotNull() {

		final Date result = testObj.getDelta(batchJobContextMock);

		assertThat(result).isEqualTo(DELTA);
	}

	@Test
	void getDelta_ShouldReturnDeltaFromService_WhenItIsNullInJobDetailParams() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
		testObj.extractionMaxDays = EXTRACTION_MAX_DAYS;

		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA_KEY))
				.thenReturn(null);
		when(batchJobContextMock.getJobName()).thenReturn("JOBNAME");

		when(batchJobTrackingServiceMock.findLastJobExecutionWithNonEmptyExtraction(eq("JOBNAME"),
				any(LocalDateTime.class))).thenReturn(Optional.of(batchJobTrackInfoEntityMock));

		when(batchJobTrackInfoEntityMock.getStartTime()).thenReturn(TimeMachine.now());

		final Date result = testObj.getDelta(batchJobContextMock);

		verify(batchJobTrackingServiceMock).findLastJobExecutionWithNonEmptyExtraction(eq("JOBNAME"),
				searchJobFromArgumentCaptor.capture());

		assertThat(result).isEqualTo(DateUtil.convertToDate(TimeMachine.now(), UTC));
		assertThat(searchJobFromArgumentCaptor.getValue()).isEqualTo(TimeMachine.now().minusDays(EXTRACTION_MAX_DAYS));
	}

	@Test
	void getDelta_ShouldReturnExtractionMaxDays_WhenServiceDontFindResults() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
		testObj.extractionMaxDays = EXTRACTION_MAX_DAYS;

		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(DELTA_KEY))
				.thenReturn(null);
		when(batchJobContextMock.getJobName()).thenReturn("JOBNAME");

		when(batchJobTrackingServiceMock.findLastJobExecutionWithNonEmptyExtraction(eq("JOBNAME"),
				any(LocalDateTime.class))).thenReturn(Optional.empty());

		final Date result = testObj.getDelta(batchJobContextMock);

		verify(batchJobTrackingServiceMock).findLastJobExecutionWithNonEmptyExtraction(eq("JOBNAME"),
				searchJobFromArgumentCaptor.capture());

		Date expectedDate = DateUtil.convertToDate(TimeMachine.now().minusDays(EXTRACTION_MAX_DAYS), UTC);
		assertThat(result).isEqualTo(expectedDate);
	}

	private static class MyAbstractDeltaBatchJobItemsExtractor
			extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> {

		private MyAbstractDeltaBatchJobItemsExtractor(final BatchJobTrackingService batchJobTrackingService) {
			super(batchJobTrackingService);
		}

		@Override
		protected Collection<BatchJobItem<Object>> getItems(BatchJobContext ctx, final Date delta) {
			return List.of();
		}

	}

}
