package com.paypal.infrastructure.batchjob.listeners;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

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

	@Test
	void onItemItemExtractionSuccessful_ShouldCallBatchJobFailedItemServiceItemProcessedMethod() {

		testObj.onItemExtractionSuccessful(batchJobContextMock, List.of(batchJobItemMock));

		verify(batchJobFailedItemServiceMock).checkUpdatedFailedItems(argThat(list -> list.contains(batchJobItemMock)));
	}

}
