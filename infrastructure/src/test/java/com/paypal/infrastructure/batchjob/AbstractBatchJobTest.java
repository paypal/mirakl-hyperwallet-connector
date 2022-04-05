package com.paypal.infrastructure.batchjob;

import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.infrastructure.batchjob.listeners.LoggingBatchJobItemProcessingListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractBatchJobTest {

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForType(AbstractBatchJob.class);

	public static final String MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER = "Error while invoking batch job listener";

	private static final String KEY_BATCH_JOB_STATUS = "batchJobStatus";

	private AbstractBatchJob<BatchJobContext, BatchJobItem<Object>> testObj;

	@Mock
	private BatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> batchJobItemBatchJobItemsExtractor;

	@Mock
	private BatchJobItemProcessor<BatchJobContext, BatchJobItem<Object>> itemBatchJobItemProcessor;

	@Mock
	private BatchJobProcessingListener<BatchJobContext, BatchJobItem<Object>> listenerMock1, listenerMock2;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Mock
	private JobDetail jobDetailMock;

	@Mock
	private JobDataMap jobDataMapMock;

	@Mock
	private BatchJobItem<Object> itemMock1, itemMock2;

	@BeforeEach
	public void setUp() {
		when(jobExecutionContextMock.getJobDetail()).thenReturn(jobDetailMock);
		when(jobDetailMock.getJobDataMap()).thenReturn(jobDataMapMock);
		testObj = new MyAbstractBatchJob(itemBatchJobItemProcessor, batchJobItemBatchJobItemsExtractor);
		testObj.setBatchJobProcessingListeners(List.of(listenerMock1, listenerMock2));
		lenient().when(batchJobItemBatchJobItemsExtractor.getItems(any(BatchJobContext.class)))
				.thenReturn(List.of(itemMock1, itemMock2));
	}

	@Test
	void execute_ShouldRetrieveAndProcessBatchItems() throws JobExecutionException {

		testObj.execute(jobExecutionContextMock);

		final InOrder inOrder = Mockito.inOrder(jobDataMapMock, listenerMock1, listenerMock2,
				batchJobItemBatchJobItemsExtractor, itemBatchJobItemProcessor);

		inOrder.verify(jobDataMapMock).put(KEY_BATCH_JOB_STATUS, BatchJobStatus.RUNNING);
		inOrder.verify(listenerMock1).onBatchJobStarted(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).onBatchJobStarted(any(BatchJobContext.class));
		inOrder.verify(listenerMock1).beforeItemExtraction(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).beforeItemExtraction(any(BatchJobContext.class));
		inOrder.verify(batchJobItemBatchJobItemsExtractor).getItems(any(BatchJobContext.class));
		inOrder.verify(listenerMock1).onItemExtractionSuccessful(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).onItemExtractionSuccessful(any(BatchJobContext.class));
		inOrder.verify(listenerMock1).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock2).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(itemBatchJobItemProcessor).processItem(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock1).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock2).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock1));
		inOrder.verify(listenerMock1).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock2).beforeProcessingItem(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(itemBatchJobItemProcessor).processItem(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock1).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(listenerMock2).onItemProcessingSuccess(any(BatchJobContext.class), eq(itemMock2));
		inOrder.verify(jobDataMapMock).put(KEY_BATCH_JOB_STATUS, BatchJobStatus.FINISHED);
		inOrder.verify(listenerMock1).onBatchJobFinished(any(BatchJobContext.class));
		inOrder.verify(listenerMock2).onBatchJobFinished(any(BatchJobContext.class));
	}

	@Test
	void execute_ShouldLogAnError_WhenOnBatchJobStartedThrowsARunTimeException() throws JobExecutionException {

		doThrow(RuntimeException.class).when(listenerMock1).onBatchJobStarted(any(BatchJobContext.class));

		testObj.execute(jobExecutionContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	@Test
	void execute_ShouldLogAnError_WhenBeforeItemExtractionThrowsARunTimeException() throws JobExecutionException {

		doThrow(RuntimeException.class).when(listenerMock1).beforeItemExtraction(any(BatchJobContext.class));

		testObj.execute(jobExecutionContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	@Test
	void execute_ShouldLogAnError_WhenOnItemExtractionSuccessfulThrowsARunTimeException() throws JobExecutionException {

		doThrow(RuntimeException.class).when(listenerMock1).onItemExtractionSuccessful(any(BatchJobContext.class));

		testObj.execute(jobExecutionContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	@Test
	void execute_ShouldCallOnItemExtractionFailure_WhenBatchJobItemBatchJobItemsExtractorThrowsARunTimeException()
			throws JobExecutionException {

		doThrow(RuntimeException.class).when(batchJobItemBatchJobItemsExtractor).getItems(any(BatchJobContext.class));

		testObj.execute(jobExecutionContextMock);

		verify(listenerMock1).onItemExtractionFailure(any(BatchJobContext.class), any(RuntimeException.class));
		verify(listenerMock2).onItemExtractionFailure(any(BatchJobContext.class), any(RuntimeException.class));
	}

	@Test
	void execute_ShouldLogAnError_WhenOnItemExtractionFailureThrowsARunTimeException() throws JobExecutionException {

		doThrow(RuntimeException.class).when(batchJobItemBatchJobItemsExtractor).getItems(any(BatchJobContext.class));
		doThrow(RuntimeException.class).when(listenerMock1).onItemExtractionFailure(any(BatchJobContext.class),
				any(RuntimeException.class));

		testObj.execute(jobExecutionContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	@Test
	void execute_ShouldLogAnError_WhenOnBatchJobFailureThrowsARunTimeException() throws JobExecutionException {

		doThrow(RuntimeException.class).when(batchJobItemBatchJobItemsExtractor).getItems(any(BatchJobContext.class));
		doThrow(RuntimeException.class).when(listenerMock1).onBatchJobFailure(any(BatchJobContext.class),
				any(RuntimeException.class));

		testObj.execute(jobExecutionContextMock);

		assertThat(logTrackerStub.contains(MSG_ERROR_WHILE_INVOKING_BATCH_JOB_LISTENER)).isTrue();
	}

	private static class MyAbstractBatchJob extends AbstractBatchJob<BatchJobContext, BatchJobItem<Object>> {

		private final BatchJobItemProcessor<BatchJobContext, BatchJobItem<Object>> itemBatchJobItemProcessor;

		private final BatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> batchJobItemBatchJobItemsExtractor;

		private MyAbstractBatchJob(
				final BatchJobItemProcessor<BatchJobContext, BatchJobItem<Object>> itemBatchJobItemProcessor,
				final BatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> batchJobItemBatchJobItemsExtractor) {
			this.itemBatchJobItemProcessor = itemBatchJobItemProcessor;
			this.batchJobItemBatchJobItemsExtractor = batchJobItemBatchJobItemsExtractor;
		}

		@Override
		protected BatchJobItemProcessor<BatchJobContext, BatchJobItem<Object>> getBatchJobItemProcessor() {
			return itemBatchJobItemProcessor;
		}

		@Override
		protected BatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> getBatchJobItemsExtractor() {
			return batchJobItemBatchJobItemsExtractor;
		}

	}

}
