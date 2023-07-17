package com.paypal.observability.loggingcontext.batchjobs.listeners;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItemValidationResult;
import com.paypal.observability.batchjoblogging.listeners.BatchJobLoggingListener;
import com.paypal.observability.batchjoblogging.service.BatchJobLoggingContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Optional;

import static org.apache.commons.lang3.ArrayUtils.EMPTY_THROWABLE_ARRAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchJobLoggingListenerTest {

	public static final String JOB_NAME = "jobName";

	public static final int NUMBER_OF_ITEMS_TO_BE_PROCESSED = 22;

	public static final String ITEM_TYPE = "itemType";

	public static final String ITEM_ID = "itemId";

	public static final int NUMBER_OF_ITEMS_PROCESSED = 20;

	public static final int NUMBER_OF_ITEMS_FAILED = 2;

	public static final int NUMBER_OF_ITEMS_REMAINING = 0;

	public static final int NUMBER_OF_ITEMS_FAILED_DURING_EXTRACTION = 5;

	@InjectMocks
	private BatchJobLoggingListener testObj;

	@Mock
	private BatchJobLoggingContextService batchJobLoggingContextServiceMock;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForType(BatchJobLoggingListener.class);

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private BatchJobItem<?> batchJobItemMock;

	@Mock
	private BatchJobItemValidationResult batchJobItemValidationResultMock;

	@Mock
	private Collection<BatchJobItem<?>> extractedItemsMock;

	@Mock
	private Exception exceptionMock;

	@BeforeEach
	public void setUp() {
		lenient().when(batchJobContextMock.getJobName()).thenReturn(JOB_NAME);
		lenient().when(batchJobContextMock.getNumberOfItemsToBeProcessed()).thenReturn(NUMBER_OF_ITEMS_TO_BE_PROCESSED);
		lenient().when(batchJobContextMock.getNumberOfItemsProcessed()).thenReturn(NUMBER_OF_ITEMS_PROCESSED);
		lenient().when(batchJobContextMock.getNumberOfItemsFailed()).thenReturn(NUMBER_OF_ITEMS_FAILED);
		lenient().when(batchJobContextMock.getNumberOfItemsRemaining()).thenReturn(NUMBER_OF_ITEMS_REMAINING);
		lenient().when(batchJobItemMock.getItemType()).thenReturn(ITEM_TYPE);
		lenient().when(batchJobItemMock.getItemId()).thenReturn(ITEM_ID);
		lenient().when(batchJobContextMock.isPartialItemExtraction()).thenReturn(false);

	}

	@Test
	void beforeItemExtraction_ShouldLogAnInfoMessage() {

		testObj.beforeItemExtraction(batchJobContextMock);

		assertThat(logTrackerStub.contains("Starting extraction of items to be processed")).isTrue();
	}

	@Test
	void onItemExtractionSuccessful_ShouldLogAnInfoMessage_WhenExtractionIsNotPartial() {
		testObj.onItemExtractionSuccessful(batchJobContextMock, extractedItemsMock);

		assertThat(logTrackerStub.contains(
				"Retrieved the following number of items to be processed: " + NUMBER_OF_ITEMS_TO_BE_PROCESSED))
						.isTrue();
	}

	@Test
	void onItemExtractionSuccessful_ShouldLogWarnMessage_WhenExtractionIsPartial() {
		when(batchJobContextMock.isPartialItemExtraction()).thenReturn(true);
		testObj.onItemExtractionSuccessful(batchJobContextMock, extractedItemsMock);

		assertThat(logTrackerStub.contains("Some of the items to be processed couldn't be retrieved. "
				+ "Only the following number of items were retrieved and are going to be processed "
				+ NUMBER_OF_ITEMS_TO_BE_PROCESSED)).isTrue();
	}

	@Test
	void onItemExtractionSuccessful_ShouldLogWarnMessage_WhenExtractionIsPartial_AndNumberOfFailedItemsIsKnown() {
		when(batchJobContextMock.isPartialItemExtraction()).thenReturn(true);
		when(batchJobContextMock.getNumberOfItemsNotSuccessfullyExtracted())
				.thenReturn(Optional.of(NUMBER_OF_ITEMS_FAILED_DURING_EXTRACTION));

		testObj.onItemExtractionSuccessful(batchJobContextMock, extractedItemsMock);

		assertThat(logTrackerStub.contains("Some of the items to be processed couldn't be retrieved. "
				+ "Only the following number of items were retrieved and is going to be processed "
				+ NUMBER_OF_ITEMS_TO_BE_PROCESSED)).isFalse();
		assertThat(logTrackerStub
				.contains("Retrieved the following number of items to be processed: " + NUMBER_OF_ITEMS_TO_BE_PROCESSED
						+ ". " + "Additionally there are " + NUMBER_OF_ITEMS_FAILED_DURING_EXTRACTION
						+ " items that couldn't be retrieved and can't be processed")).isTrue();

	}

	@Test
	void onItemExtractionFailure_ShouldLogAnErrorMessage() {
		when(exceptionMock.getSuppressed()).thenReturn(EMPTY_THROWABLE_ARRAY);

		logTrackerStub.recordForLevel(LogTracker.LogLevel.ERROR);

		testObj.onItemExtractionFailure(batchJobContextMock, exceptionMock);

		assertThat(logTrackerStub.contains("Failed retrieval of items")).isTrue();
	}

	@Test
	void beforeProcessingItem_ShouldLogAnInfoMessage() {
		testObj.beforeProcessingItem(batchJobContextMock, batchJobItemMock);

		assertThat(logTrackerStub.contains("Processing item of type " + ITEM_TYPE + " with id: " + ITEM_ID)).isTrue();
	}

	@Test
	void onItemProcessingFailure_ShouldLogAnInfoAndAnErrorMessage() {
		when(exceptionMock.getSuppressed()).thenReturn(EMPTY_THROWABLE_ARRAY);

		logTrackerStub.recordForLevel(LogTracker.LogLevel.ERROR).recordForLevel(LogTracker.LogLevel.INFO);

		testObj.onItemProcessingFailure(batchJobContextMock, batchJobItemMock, exceptionMock);

		assertThat(logTrackerStub.contains("Failed processing item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + NUMBER_OF_ITEMS_REMAINING + " items remaining"))
						.isTrue();

	}

	@Test
	void onItemProcessingFailure_ShouldLogAnInfoAnErrorAndWarnMessage_WhenExtractionWasPartial() {
		when(exceptionMock.getSuppressed()).thenReturn(EMPTY_THROWABLE_ARRAY);
		when(batchJobContextMock.isPartialItemExtraction()).thenReturn(true);

		logTrackerStub.recordForLevel(LogTracker.LogLevel.ERROR).recordForLevel(LogTracker.LogLevel.INFO);

		testObj.onItemProcessingFailure(batchJobContextMock, batchJobItemMock, exceptionMock);

		assertThat(logTrackerStub.contains("Failed processing item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + NUMBER_OF_ITEMS_REMAINING + " items remaining"))
						.isTrue();

		assertThat(logTrackerStub.contains("Not all items were able to be retrieved during the extraction phase,"
				+ " so there are additional items that couldn't be processed since they weren't retrieved.")).isTrue();
	}

	@Test
	void onItemProcessingFailure_ShouldLogAnInfoAnErrorAndWarnMessage_WhenExtractionWasPartialAndFailedItemsAreKnown() {
		when(exceptionMock.getSuppressed()).thenReturn(EMPTY_THROWABLE_ARRAY);
		when(batchJobContextMock.isPartialItemExtraction()).thenReturn(true);
		when(batchJobContextMock.getNumberOfItemsNotSuccessfullyExtracted())
				.thenReturn(Optional.of(NUMBER_OF_ITEMS_FAILED_DURING_EXTRACTION));

		logTrackerStub.recordForLevel(LogTracker.LogLevel.ERROR).recordForLevel(LogTracker.LogLevel.INFO);

		testObj.onItemProcessingFailure(batchJobContextMock, batchJobItemMock, exceptionMock);

		assertThat(logTrackerStub.contains("Failed processing item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + NUMBER_OF_ITEMS_REMAINING + " items remaining"))
						.isTrue();

		assertThat(logTrackerStub.contains("Not all items were able to be retrieved during the extraction phase,"
				+ " so there are additional items that couldn't be processed since they weren't retrieved.")).isFalse();
		assertThat(logTrackerStub.contains("Additionally there were " + NUMBER_OF_ITEMS_FAILED_DURING_EXTRACTION
				+ " items that couldn't be retrieved during the extraction phase," + " so they were not processed."))
						.isTrue();
	}

	@Test
	void onItemProcessingSuccess_ShouldLogInfoMessages() {

		testObj.onItemProcessingSuccess(batchJobContextMock, batchJobItemMock);

		assertThat(logTrackerStub.contains("Processed successfully item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + NUMBER_OF_ITEMS_REMAINING + " items remaining"))
						.isTrue();

	}

	@Test
	void onItemProcessingSuccess_ShouldLogInfoMessagesAndWarnMessage_WhenExtractionWasPartialAndIsLastItem() {
		when(batchJobContextMock.isPartialItemExtraction()).thenReturn(true);
		when(batchJobContextMock.getNumberOfItemsRemaining()).thenReturn(1);
		testObj.onItemProcessingSuccess(batchJobContextMock, batchJobItemMock);

		assertThat(logTrackerStub.contains("Processed successfully item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + 1 + " items remaining")).isTrue();
		assertThat(logTrackerStub.contains("Not all items were able to be retrieved during the extraction phase,"
				+ " so there are additional items that couldn't be processed since they weren't retrieved.")).isFalse();

		when(batchJobContextMock.getNumberOfItemsRemaining()).thenReturn(0);
		testObj.onItemProcessingSuccess(batchJobContextMock, batchJobItemMock);

		assertThat(logTrackerStub.contains("Processed successfully item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + 0 + " items remaining")).isTrue();
		assertThat(logTrackerStub.contains("Not all items were able to be retrieved during the extraction phase,"
				+ " so there are additional items that couldn't be processed since they weren't retrieved.")).isTrue();
	}

	@Test
	void onItemProcessingSuccess_ShouldLogInfoMessagesAndWarnMessage_WhenExtractionWasPartialAndIsLastItemAndNumberOfFailedItemsIsKnown() {
		when(batchJobContextMock.isPartialItemExtraction()).thenReturn(true);
		when(batchJobContextMock.getNumberOfItemsRemaining()).thenReturn(1);
		when(batchJobContextMock.getNumberOfItemsNotSuccessfullyExtracted())
				.thenReturn(Optional.of(NUMBER_OF_ITEMS_FAILED_DURING_EXTRACTION));

		testObj.onItemProcessingSuccess(batchJobContextMock, batchJobItemMock);

		assertThat(logTrackerStub.contains("Processed successfully item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + 1 + " items remaining")).isTrue();
		assertThat(logTrackerStub.contains("Not all items were able to be retrieved during the extraction phase,"
				+ " so there are additional items that couldn't be processed since they weren't retrieved.")).isFalse();

		when(batchJobContextMock.getNumberOfItemsRemaining()).thenReturn(0);
		testObj.onItemProcessingSuccess(batchJobContextMock, batchJobItemMock);

		assertThat(logTrackerStub.contains("Processed successfully item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + 0 + " items remaining")).isTrue();
		assertThat(logTrackerStub.contains("Not all items were able to be retrieved during the extraction phase,"
				+ " so there are additional items that couldn't be processed since they weren't retrieved.")).isFalse();
		assertThat(logTrackerStub.contains("Additionally there were " + NUMBER_OF_ITEMS_FAILED_DURING_EXTRACTION
				+ " items that couldn't be retrieved during the extraction phase," + " so they were not processed."))
						.isTrue();
	}

	@Test
	void onItemProcessingValidationFailure_ShouldLogWarnMessage() {
		testObj.onItemProcessingValidationFailure(batchJobContextMock, batchJobItemMock,
				batchJobItemValidationResultMock);

		assertThat(logTrackerStub
				.contains("Validation of item of type " + ITEM_TYPE + " with id: " + ITEM_ID + " has failed")).isTrue();
	}

}
