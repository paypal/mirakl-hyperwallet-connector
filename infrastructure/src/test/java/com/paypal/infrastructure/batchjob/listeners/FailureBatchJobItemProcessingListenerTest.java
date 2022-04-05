package com.paypal.infrastructure.batchjob.listeners;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

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

}
