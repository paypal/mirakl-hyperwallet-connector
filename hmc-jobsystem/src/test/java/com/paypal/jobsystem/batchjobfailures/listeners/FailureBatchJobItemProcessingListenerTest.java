package com.paypal.jobsystem.batchjobfailures.listeners;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import com.paypal.jobsystem.batchjobfailures.listeners.FailureBatchJobItemProcessingListener;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FailureBatchJobItemProcessingListenerTest {

	@InjectMocks
	private FailureBatchJobItemProcessingListener testObj;

	@Mock
	private BatchJobFailedItemService batchJobFailedItemServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private BatchJobItem<Object> batchJobItemMock;

	@Mock
	private BatchJob batchJobMock;

	@Mock
	private Exception exceptionMock;

	@Test
	void onItemProcessingFailure_ShouldCallBatchJobFailedItemServiceItemFailedMethod() {

		testObj.onItemProcessingFailure(batchJobContextMock, batchJobItemMock, exceptionMock);

		verify(batchJobFailedItemServiceMock).saveItemFailed(batchJobItemMock);
	}

	@Test
	void onItemProcessingSuccess_ShouldCallBatchJobFailedItemServiceItemProcessedMethod() {

		testObj.onItemProcessingFailure(batchJobContextMock, batchJobItemMock, exceptionMock);

		verify(batchJobFailedItemServiceMock).saveItemFailed(batchJobItemMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	void onItemItemExtractionSuccessful_ShouldCallBatchJobFailedItemServiceItemProcessedMethod_ForExtractJobs() {
		when(batchJobContextMock.getBatchJob()).thenReturn(batchJobMock);
		when(batchJobMock.getType()).thenReturn(BatchJobType.EXTRACT);

		testObj.onItemExtractionSuccessful(batchJobContextMock, List.of(batchJobItemMock));

		verify(batchJobFailedItemServiceMock).checkUpdatedFailedItems(argThat(list -> list.contains(batchJobItemMock)));
	}

	@SuppressWarnings("unchecked")
	@Test
	void onItemItemExtractionSuccessful_ShouldCallBatchJobFailedItemServiceItemProcessedMethod_ForRetryJobs() {
		when(batchJobContextMock.getBatchJob()).thenReturn(batchJobMock);
		when(batchJobMock.getType()).thenReturn(BatchJobType.RETRY);

		testObj.onItemExtractionSuccessful(batchJobContextMock, List.of(batchJobItemMock));

		verify(batchJobFailedItemServiceMock, times(0)).checkUpdatedFailedItems(any());
	}

}
