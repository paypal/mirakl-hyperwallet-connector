package com.paypal.infrastructure.batchjob;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AbstractFailedItemsBatchJobItemsExtractorTest {

	private static final String ITEM_TYPE = "itemType";

	@Mock
	private BatchJobFailedItemService batchJobFailedItemServiceMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldCallBatchJobFailedItemService() {

		final AbstractFailedItemsBatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> testObj = new MyAbstractFailedItemsBatchJobItemsExtractor(
				ITEM_TYPE, batchJobFailedItemServiceMock);

		testObj.getItems(batchJobContextMock);

		verify(batchJobFailedItemServiceMock).getFailedItemsForRetry(ITEM_TYPE);
	}

	private static class MyAbstractFailedItemsBatchJobItemsExtractor
			extends AbstractFailedItemsBatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> {

		protected MyAbstractFailedItemsBatchJobItemsExtractor(final String itemType,
				final BatchJobFailedItemService batchJobFailedItemService) {
			super(itemType, batchJobFailedItemService);
		}

		@Override
		protected Collection<BatchJobItem<Object>> getBatchJobFailedItems(
				List<BatchJobFailedItem> batchJobFailedItems) {
			return List.of();
		}

	}

}
