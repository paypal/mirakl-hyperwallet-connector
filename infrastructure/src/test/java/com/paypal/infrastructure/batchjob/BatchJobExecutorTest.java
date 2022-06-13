package com.paypal.infrastructure.batchjob;

import com.callibrity.logging.test.LogTrackerStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchJobExecutorTest {

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForType(BatchJobExecutor.class);

	public static final String MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER = "Error while invoking batch job listener";

	@InjectMocks
	private BatchJobExecutor testObj;

	@Mock
	private BatchJob<BatchJobContext, BatchJobItem<Object>> batchJobMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private BatchJobProcessingListener listenerMock1, listenerMock2;

	@Mock
	private BatchJobItem<Object> itemMock1, itemMock2;

	@Mock
	private BatchJobItem<Object> enrichedItemMock1, enrichedItemMock2;

	private Collection<BatchJobItem<Object>> itemCollection;

	@BeforeEach
	public void setUp() {
		testObj.batchJobProcessingListeners = List.of(listenerMock1, listenerMock2);
		itemCollection = List.of(itemMock1, itemMock2);
		lenient().when(batchJobMock.getItems(any(BatchJobContext.class))).thenReturn(itemCollection);

		lenient().when(batchJobMock.validateItem(any(), any()))
				.thenReturn(BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.VALID).build());
		lenient().when(batchJobMock.enrichItem(any(BatchJobContext.class), eq(itemMock1)))
				.thenReturn(enrichedItemMock1);
		lenient().when(batchJobMock.enrichItem(any(BatchJobContext.class), eq(itemMock2)))
				.thenReturn(enrichedItemMock2);
	}

	@SuppressWarnings("unchecked")
	@Test
	void execute_ShouldRetrieveAndProcessBatchItems() {

		testObj.execute(batchJobMock, batchJobContextMock);

		final InOrder inOrder = Mockito.inOrder(listenerMock1, listenerMock2, batchJobMock);

		inOrder.verify(listenerMock1).onBatchJobStarted(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).onBatchJobStarted(any(BatchJobContext.class));
		inOrder.verify(listenerMock1).beforeItemExtraction(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).beforeItemExtraction(any(BatchJobContext.class));
		inOrder.verify(batchJobMock).getItems(any(BatchJobContext.class));
		inOrder.verify(listenerMock1).onItemExtractionSuccessful(any(BatchJobContext.class),
				(Collection) eq(itemCollection));
		inOrder.verify(listenerMock2).onItemExtractionSuccessful(any(BatchJobContext.class),
				(Collection) eq(itemCollection));

		inOrder.verify(batchJobMock).prepareForItemProcessing(any(BatchJobContext.class), any());

		inOrder.verify(listenerMock1).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock2).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(batchJobMock).enrichItem(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(batchJobMock).validateItem(any(BatchJobContext.class), eq(enrichedItemMock1));
		inOrder.verify(batchJobMock).processItem(any(BatchJobContext.class), eq(enrichedItemMock1));
		inOrder.verify(listenerMock1).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock2).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock1).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock2).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(batchJobMock).enrichItem(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(batchJobMock).validateItem(any(BatchJobContext.class), eq(enrichedItemMock2));
		inOrder.verify(batchJobMock).processItem(any(BatchJobContext.class), eq(enrichedItemMock2));
		inOrder.verify(listenerMock1).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock2).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock1).onBatchJobFinished(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).onBatchJobFinished(any(BatchJobContext.class));
	}

	@Test
	void execute_ShouldContinueProcessing_WhenItemValidationReturnsAWarning() {

		when(batchJobMock.validateItem(any(), eq(enrichedItemMock2))).thenReturn(
				BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.WARNING).build());

		testObj.execute(batchJobMock, batchJobContextMock);

		verify(batchJobMock).enrichItem(any(BatchJobContext.class), eq(itemMock2));
		verify(batchJobMock).validateItem(any(BatchJobContext.class), eq(enrichedItemMock2));
		verify(batchJobMock).processItem(any(BatchJobContext.class), eq(enrichedItemMock2));
		verify(listenerMock1).onItemProcessingValidationFailure(any(BatchJobContext.class), eq(itemMock2),
				any(BatchJobItemValidationResult.class));
		verify(listenerMock2).onItemProcessingValidationFailure(any(BatchJobContext.class), eq(itemMock2),
				any(BatchJobItemValidationResult.class));
		verify(listenerMock1).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock2));
		verify(listenerMock2).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock2));
	}

	@Test
	void execute_ShouldAbortItemProcessingAndRegisterFailure_WhenItemValidationReturnsAnInvalid() {

		when(batchJobMock.validateItem(any(), eq(enrichedItemMock2))).thenReturn(
				BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.INVALID).build());

		testObj.execute(batchJobMock, batchJobContextMock);

		verify(batchJobMock).enrichItem(any(BatchJobContext.class), eq(itemMock2));
		verify(batchJobMock).validateItem(any(BatchJobContext.class), eq(enrichedItemMock2));
		verify(batchJobMock, times(0)).processItem(any(BatchJobContext.class), eq(enrichedItemMock2));
		verify(listenerMock1).onItemProcessingValidationFailure(any(BatchJobContext.class), eq(itemMock2),
				any(BatchJobItemValidationResult.class));
		verify(listenerMock2).onItemProcessingValidationFailure(any(BatchJobContext.class), eq(itemMock2),
				any(BatchJobItemValidationResult.class));
		verify(listenerMock1).onItemProcessingFailure(any(BatchJobContext.class), eq(itemMock2), eq(null));
		verify(listenerMock2).onItemProcessingFailure(any(BatchJobContext.class), eq(itemMock2), eq(null));
	}

	@Test
	void execute_ShouldLogAnError_WhenOnBatchJobStartedThrowsARunTimeException() {

		doThrow(RuntimeException.class).when(listenerMock1).onBatchJobStarted(any(BatchJobContext.class));

		testObj.execute(batchJobMock, batchJobContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	@Test
	void execute_ShouldLogAnError_WhenBeforeItemExtractionThrowsARunTimeException() {

		doThrow(RuntimeException.class).when(listenerMock1).beforeItemExtraction(any(BatchJobContext.class));

		testObj.execute(batchJobMock, batchJobContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	@Test
	void execute_ShouldLogAnError_WhenOnItemExtractionSuccessfulThrowsARunTimeException() {

		doThrow(RuntimeException.class).when(listenerMock1).onItemExtractionSuccessful(any(BatchJobContext.class),
				any());

		testObj.execute(batchJobMock, batchJobContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	@Test
	void execute_ShouldCallOnItemExtractionFailure_WhenBatchJobItemBatchJobItemsExtractorThrowsARunTimeException() {

		doThrow(RuntimeException.class).when(batchJobMock).getItems(any(BatchJobContext.class));

		testObj.execute(batchJobMock, batchJobContextMock);

		verify(listenerMock1).onItemExtractionFailure(any(BatchJobContext.class), any(RuntimeException.class));
		verify(listenerMock2).onItemExtractionFailure(any(BatchJobContext.class), any(RuntimeException.class));
	}

	@Test
	void execute_ShouldLogAnError_WhenOnItemExtractionFailureThrowsARunTimeException() {

		doThrow(RuntimeException.class).when(batchJobMock).getItems(any(BatchJobContext.class));
		doThrow(RuntimeException.class).when(listenerMock1).onItemExtractionFailure(any(BatchJobContext.class),
				any(RuntimeException.class));

		testObj.execute(batchJobMock, batchJobContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	@Test
	void execute_ShouldCallOnPreparationForProcessingFailure_WhenPreparationForProcessingThrowsARunTimeException() {

		doThrow(RuntimeException.class).when(batchJobMock).prepareForItemProcessing(any(), any());

		testObj.execute(batchJobMock, batchJobContextMock);

		verify(listenerMock1).onPreparationForProcessingFailure(any(BatchJobContext.class),
				any(RuntimeException.class));
		verify(listenerMock2).onPreparationForProcessingFailure(any(BatchJobContext.class),
				any(RuntimeException.class));
	}

	@Test
	void execute_ShouldLogAnError_WhenOnPreparationForProcessingFailureThrowsARunTimeException() {

		doThrow(RuntimeException.class).when(batchJobMock).prepareForItemProcessing(any(), any());
		doThrow(RuntimeException.class).when(listenerMock1)
				.onPreparationForProcessingFailure(any(BatchJobContext.class), any(RuntimeException.class));

		testObj.execute(batchJobMock, batchJobContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	@Test
	void execute_ShouldLogAnError_WhenOnBatchJobFailureThrowsARunTimeException() {

		doThrow(RuntimeException.class).when(batchJobMock).getItems(any(BatchJobContext.class));
		doThrow(RuntimeException.class).when(listenerMock1).onBatchJobFailure(any(BatchJobContext.class),
				any(RuntimeException.class));

		testObj.execute(batchJobMock, batchJobContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

}
