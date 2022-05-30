package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheFailureResolvePolicy;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheFailureResolver;
import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractCachingFailedItemsBatchJobItemsExtractorTest {

	@InjectMocks
	@Spy
	private MyCachingFailedItemsBatchJobItemsExtractorTest testObj;

	@Mock
	private BatchJobFailedItemCacheService batchJobFailedItemCacheServiceMock;

	@Mock
	private BatchJobFailedItem batchJobFailedItem1Mock, batchJobFailedItem2Mock;

	@Mock
	private MyItem batchJobItem1Mock, batchJobItem2Mock;

	@Mock
	private List<BatchJobFailedItem> batchJobFailedItemsMock;

	@Mock
	private BatchJobFailedItemCacheFailureResolvePolicy batchJobFailedItemCacheFailureResolvePolicyMock;

	@Captor
	private ArgumentCaptor<BatchJobFailedItemCacheFailureResolver> cacheFailureResolverCaptor;

	@Test
	void getBatchJobFailedItems_shouldRetrieveItemsFromCache() {
		HashMap<BatchJobFailedItem, Optional<MyItem>> cacheResponse = buildCacheResponse();
		when(batchJobFailedItemCacheServiceMock.retrieveAllItems(eq(MyItem.class), any(), any()))
				.thenReturn(cacheResponse);

		Collection<MyItem> result = testObj.getBatchJobFailedItems(batchJobFailedItemsMock);
		verify(testObj, times(0)).getItems((List<String>) any());

		assertThat(result).containsExactlyInAnyOrder(batchJobItem1Mock, batchJobItem2Mock);
	}

	@Test
	void getBatchJobFailedItems_shouldPassGetItemsMethodAsCacheResolver() {
		HashMap<BatchJobFailedItem, Optional<MyItem>> cacheResponse = buildCacheResponse();
		when(batchJobFailedItemCacheServiceMock.retrieveAllItems(eq(MyItem.class), any(), any()))
				.thenReturn(cacheResponse);

		testObj.getBatchJobFailedItems(batchJobFailedItemsMock);

		verify(batchJobFailedItemCacheServiceMock, times(1)).retrieveAllItems(any(), any(),
				cacheFailureResolverCaptor.capture());

		// we use MyList custom type to check if is passing getItems as cache failure
		// resolver
		assertThat((cacheFailureResolverCaptor.getValue()).itemsToBeCached(Collections.emptyList()))
				.isInstanceOf(MyList.class);
	}

	@Test
	void getBatchJobFailedItems_shouldPassCustomCacheResolvePolicy() {
		HashMap<BatchJobFailedItem, Optional<MyItem>> cacheResponse = buildCacheResponse();
		when(batchJobFailedItemCacheServiceMock.retrieveAllItems(eq(MyItem.class), any(), any(), any()))
				.thenReturn(cacheResponse);

		testObj.policy = Optional.of(batchJobFailedItemCacheFailureResolvePolicyMock);
		testObj.getBatchJobFailedItems(batchJobFailedItemsMock);

		verify(batchJobFailedItemCacheServiceMock, times(1)).retrieveAllItems(any(), any(),
				cacheFailureResolverCaptor.capture(), eq(batchJobFailedItemCacheFailureResolvePolicyMock));
	}

	private HashMap<BatchJobFailedItem, Optional<MyItem>> buildCacheResponse() {
		HashMap<BatchJobFailedItem, Optional<MyItem>> cacheResponse = new HashMap<>();
		cacheResponse.put(batchJobFailedItem1Mock, Optional.of(batchJobItem1Mock));
		cacheResponse.put(batchJobFailedItem2Mock, Optional.of(batchJobItem2Mock));
		return cacheResponse;
	}

	static class MyCachingFailedItemsBatchJobItemsExtractorTest
			extends AbstractCachingFailedItemsBatchJobItemsExtractor<BatchJobContext, MyItem> {

		Optional<BatchJobFailedItemCacheFailureResolvePolicy> policy = Optional.empty();

		protected MyCachingFailedItemsBatchJobItemsExtractorTest(String itemType,
				BatchJobFailedItemService batchJobFailedItemService,
				BatchJobFailedItemCacheService batchJobFailedItemCacheService) {
			super(MyItem.class, itemType, batchJobFailedItemService, batchJobFailedItemCacheService);
		}

		@Override
		protected Collection<MyItem> getItems(List<String> ids) {
			return new MyList<>(Collections.emptyList());
		}

		@Override
		protected Optional<BatchJobFailedItemCacheFailureResolvePolicy> getBatchJobFailedItemCacheFailureResolvePolicy() {
			return policy;
		}

	}

	static class MyItem extends AbstractBatchJobItem<String> {

		protected MyItem(String item) {
			super(item);
		}

		@Override
		public String getItemId() {
			return getItem();
		}

		@Override
		public String getItemType() {
			return this.getClass().getSimpleName();
		}

	}

	static class MyList<E> extends ArrayList<E> {

		public MyList(List<E> list) {
			super(list);
		}

	}

}
