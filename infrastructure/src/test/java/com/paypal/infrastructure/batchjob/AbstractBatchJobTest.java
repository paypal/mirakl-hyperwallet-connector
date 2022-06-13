package com.paypal.infrastructure.batchjob;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractBatchJobTest {

	@InjectMocks
	private MyAbstractBatchJob testObj;

	@Mock
	private BatchJobItemsExtractor<BatchJobContext, BatchJobItem<Object>> batchJobItemsExtractorMock;

	@Mock
	private BatchJobItemProcessor<BatchJobContext, BatchJobItem<Object>> batchJobItemProcessorMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private Collection<BatchJobItem<Object>> batchJobItemsMock;

	@Mock
	private BatchJobItem<Object> batchJobItemMock;

	@Test
	void getItems_shouldCallItemsExtractor() {
		when(batchJobItemsExtractorMock.getItems(batchJobContextMock)).thenReturn(batchJobItemsMock);

		Collection<BatchJobItem<Object>> result = testObj.getItems(batchJobContextMock);

		assertThat(result).isEqualTo(batchJobItemsMock);
	}

	@Test
	void processItem_shouldCallItemsProcessor() {
		testObj.processItem(batchJobContextMock, batchJobItemMock);

		verify(batchJobItemProcessorMock, times(1)).processItem(batchJobContextMock, batchJobItemMock);
	}

	@Test
	void getBatchJobItemValidator_shouldReturnEmptyValue() {
		assertThat(testObj.getBatchJobItemValidator()).isEmpty();
	}

	@Test
	void getBatchJobPreProcessor_shouldReturnEmptyValue() {
		assertThat(testObj.getBatchJobPreProcessor()).isEmpty();
	}

	@Test
	void getBatchJobItemEnricher_shouldReturnEmptyValue() {
		assertThat(testObj.getBatchJobItemEnricher()).isEmpty();
	}

	private static class MyAbstractBatchJob extends AbstractExtractBatchJob<BatchJobContext, BatchJobItem<Object>> {

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
