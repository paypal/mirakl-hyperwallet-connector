package com.paypal.jobsystem.batchjobsupport.support;

import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractDynamicWindowDeltaBatchJobItemsExtractorTest {

	public static final int EXTRACTION_MAX_DAYS = 30;

	@Spy
	@InjectMocks
	private MyDynamicWindowDeltaBatchJobItemsExtractor testObj;

	@Mock
	private BatchJobTrackingService batchJobTrackingServiceMock;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private BatchJobContext batchJobContextMock;

	@Mock
	private BatchJobTrackInfoEntity batchJobTrackInfoEntityMock;

	@Captor
	private ArgumentCaptor<LocalDateTime> searchJobFromArgumentCaptor;

	@Test
	void getCalculatedDelta_ShouldReturnDeltaFromService() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
		testObj.extractionMaxDays = EXTRACTION_MAX_DAYS;

		when(batchJobContextMock.getJobName()).thenReturn("JOBNAME");

		when(batchJobTrackingServiceMock.findLastJobExecutionWithNonEmptyExtraction(eq("JOBNAME"),
				any(LocalDateTime.class))).thenReturn(Optional.of(batchJobTrackInfoEntityMock));

		when(batchJobTrackInfoEntityMock.getStartTime()).thenReturn(TimeMachine.now());

		final Date result = testObj.getCalculatedDelta(batchJobContextMock);

		verify(batchJobTrackingServiceMock).findLastJobExecutionWithNonEmptyExtraction(eq("JOBNAME"),
				searchJobFromArgumentCaptor.capture());

		assertThat(result).isEqualTo(DateUtil.convertToDate(TimeMachine.now(), UTC));
		assertThat(searchJobFromArgumentCaptor.getValue()).isEqualTo(TimeMachine.now().minusDays(EXTRACTION_MAX_DAYS));
	}

	@Test
	void getCalculatedDelta_ShouldReturnExtractionMaxDays_WhenServiceDontFindResults() {
		TimeMachine.useFixedClockAt(LocalDateTime.now());
		testObj.extractionMaxDays = EXTRACTION_MAX_DAYS;

		when(batchJobContextMock.getJobName()).thenReturn("JOBNAME");

		when(batchJobTrackingServiceMock.findLastJobExecutionWithNonEmptyExtraction(eq("JOBNAME"),
				any(LocalDateTime.class))).thenReturn(Optional.empty());

		final Date result = testObj.getCalculatedDelta(batchJobContextMock);

		verify(batchJobTrackingServiceMock).findLastJobExecutionWithNonEmptyExtraction(eq("JOBNAME"),
				searchJobFromArgumentCaptor.capture());

		final Date expectedDate = DateUtil.convertToDate(TimeMachine.now().minusDays(EXTRACTION_MAX_DAYS), UTC);
		assertThat(result).isEqualTo(expectedDate);
	}

	private static class MyDynamicWindowDeltaBatchJobItemsExtractor
			extends AbstractDynamicWindowDeltaBatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> {

		private MyDynamicWindowDeltaBatchJobItemsExtractor(final BatchJobTrackingService batchJobTrackingService) {
			super(batchJobTrackingService);
		}

		@Override
		protected Collection<BatchJobItem<Object>> getItems(final BatchJobContext ctx, final Date delta) {
			return List.of();
		}

	}

}
