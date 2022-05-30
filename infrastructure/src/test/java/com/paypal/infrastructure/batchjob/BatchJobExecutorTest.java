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
	private BatchJob<BatchJobContext, BatchJobItem<?>> batchJobMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private BatchJobProcessingListener listenerMock1, listenerMock2;

	@Mock
	private BatchJobItem<?> itemMock1, itemMock2;

	private Collection<BatchJobItem<?>> itemCollection;

	@BeforeEach
	public void setUp() {
		testObj.batchJobProcessingListeners = List.of(listenerMock1, listenerMock2);
		itemCollection = List.of(itemMock1, itemMock2);
		lenient().when(batchJobMock.getItems(any(BatchJobContext.class))).thenReturn(itemCollection);
	}

	@Test
	void execute_ShouldRetrieveAndProcessBatchItems() {

		testObj.execute(batchJobMock, batchJobContextMock);

		final InOrder inOrder = Mockito.inOrder(listenerMock1, listenerMock2, batchJobMock);

		inOrder.verify(listenerMock1).onBatchJobStarted(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).onBatchJobStarted(any(BatchJobContext.class));
		inOrder.verify(listenerMock1).beforeItemExtraction(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).beforeItemExtraction(any(BatchJobContext.class));
		inOrder.verify(batchJobMock).getItems(any(BatchJobContext.class));
		inOrder.verify(listenerMock1).onItemExtractionSuccessful(any(BatchJobContext.class), eq(itemCollection));
		inOrder.verify(listenerMock2).onItemExtractionSuccessful(any(BatchJobContext.class), eq(itemCollection));
		inOrder.verify(listenerMock1).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock2).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(batchJobMock).processItem(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock1).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock2).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock1).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock2).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(batchJobMock).processItem(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock1).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock2).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock1).onBatchJobFinished(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).onBatchJobFinished(any(BatchJobContext.class));
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
	void execute_ShouldLogAnError_WhenOnBatchJobFailureThrowsARunTimeException() {

		doThrow(RuntimeException.class).when(batchJobMock).getItems(any(BatchJobContext.class));
		doThrow(RuntimeException.class).when(listenerMock1).onBatchJobFailure(any(BatchJobContext.class),
				any(RuntimeException.class));

		testObj.execute(batchJobMock, batchJobContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

}