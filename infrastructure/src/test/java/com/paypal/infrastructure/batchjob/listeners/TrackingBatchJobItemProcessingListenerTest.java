package com.paypal.infrastructure.batchjob.listeners;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobTrackingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackingBatchJobItemProcessingListenerTest {

	public static final String JOB_ID = "1234";

	public static final String JOB_NAME = "jobName";

	@Spy
	@InjectMocks
	private TrackingBatchJobItemProcessingListener testObj;

	@Mock
	private BatchJobTrackingService batchJobTrackingServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private BatchJobItem<?> batchJobItemMock;

	@Test
	void beforeItemExtraction_ShouldCallSuperBeforeItemExtraction() {

		testObj.beforeItemExtraction(batchJobContextMock);

		verify(testObj).callSuperBeforeItemExtraction(batchJobContextMock);
	}

	@Test
	void onItemExtractionSuccessful_ShouldCallBatchJobTrackingServiceTrackJobItemsAdded() {

		when(batchJobContextMock.getJobUuid()).thenReturn(JOB_ID);

		testObj.onItemExtractionSuccessful(batchJobContextMock, List.of(batchJobItemMock));

		verify(batchJobTrackingServiceMock).trackJobItemsAdded(JOB_ID, List.of(batchJobItemMock));
	}

	@Test
	void beforeItemExtraction_ShouldCallSuperOnItemExtractionFailure() {

		final Exception exception = new Exception();

		testObj.onItemExtractionFailure(batchJobContextMock, exception);

		verify(testObj).onItemExtractionFailure(batchJobContextMock, exception);
	}

	@Test
	void beforeProcessingItem_ShouldCallBatchJobTrackingServiceTrackJobItemProcessingStarted() {

		when(batchJobContextMock.getJobUuid()).thenReturn(JOB_ID);

		testObj.beforeProcessingItem(batchJobContextMock, batchJobItemMock);

		verify(batchJobTrackingServiceMock).trackJobItemProcessingStarted(JOB_ID, batchJobItemMock);
	}

	@Test
	void onItemProcessingFailure_ShouldCallBatchJobTrackingServiceTrackJobItemProcessingFinished() {

		final Exception exception = new Exception();

		when(batchJobContextMock.getJobUuid()).thenReturn(JOB_ID);

		testObj.onItemProcessingFailure(batchJobContextMock, batchJobItemMock, exception);

		verify(batchJobTrackingServiceMock).trackJobItemProcessingFinished(JOB_ID, batchJobItemMock, false);
	}

	@Test
	void onItemProcessingSuccess_ShouldCallBatchJobTrackingServiceTrackJobItemProcessingFinished() {

		when(batchJobContextMock.getJobUuid()).thenReturn(JOB_ID);

		testObj.onItemProcessingSuccess(batchJobContextMock, batchJobItemMock);

		verify(batchJobTrackingServiceMock).trackJobItemProcessingFinished(JOB_ID, batchJobItemMock, true);
	}

	@Test
	void onBatchJobStarted_ShouldCallBatchJobTrackingServiceMarkNonFinishedJobsAsAbortedAndTrackJobStart() {

		when(batchJobContextMock.getJobName()).thenReturn(JOB_NAME);
		when(batchJobContextMock.getJobUuid()).thenReturn(JOB_ID);

		testObj.onBatchJobStarted(batchJobContextMock);

		verify(batchJobTrackingServiceMock).markNonFinishedJobsAsAborted(JOB_NAME);
		verify(batchJobTrackingServiceMock).trackJobStart(JOB_ID, JOB_NAME);
	}

	@Test
	void onBatchJobFinished_ShouldCallBatchJobTrackingServiceTrackJobFinishedAsSuccessful_WhenHasNotItemsFailed() {

		when(batchJobContextMock.getJobUuid()).thenReturn(JOB_ID);
		when(batchJobContextMock.getNumberOfItemsFailed()).thenReturn(0);

		testObj.onBatchJobFinished(batchJobContextMock);

		verify(batchJobTrackingServiceMock).trackJobFinished(JOB_ID, true);
	}

	@Test
	void onBatchJobFinished_ShouldCallBatchJobTrackingServiceTrackJobFinishedAsFailed_WhenHasItemsFailed() {

		when(batchJobContextMock.getJobUuid()).thenReturn(JOB_ID);
		when(batchJobContextMock.getNumberOfItemsFailed()).thenReturn(1);

		testObj.onBatchJobFinished(batchJobContextMock);

		verify(batchJobTrackingServiceMock).trackJobFinished(JOB_ID, false);
	}

	@Test
	void onBatchJobFailure_ShouldCallBatchJobTrackingServiceTrackJobFailure() {

		when(batchJobContextMock.getJobUuid()).thenReturn(JOB_ID);
		when(batchJobContextMock.getJobName()).thenReturn(JOB_NAME);

		final Exception exception = new Exception();

		testObj.onBatchJobFailure(batchJobContextMock, exception);

		verify(batchJobTrackingServiceMock).trackJobFailure(JOB_ID, JOB_NAME);
	}

}
