package com.paypal.infrastructure.batchjob.cache;

import com.paypal.infrastructure.batchjob.BatchJobFailedItem;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchJobFailedItemCacheServiceImplTest {

	public static final String ITEM_TYPE = "itemType";

	public static final String ITEM_ID_1 = "itemId1";

	public static final String ITEM_ID_2 = "itemId2";

	@InjectMocks
	private BatchJobFailedItemCacheServiceImpl testObj;

	@Mock
	private CacheManager cacheManagerMock;

	@Mock
	private Cache cacheMock;

	@Mock
	private BatchJobItem<Object> batchJobItemMock1, batchJobItemMock2;

	@BeforeEach
	void setUp() {

		when(cacheManagerMock.getCache(ITEM_TYPE)).thenReturn(cacheMock);
	}

	@Test
	void storeItem_ShouldPutItemInCache() {

		when(batchJobItemMock1.getItemType()).thenReturn(ITEM_TYPE);
		when(batchJobItemMock1.getItemId()).thenReturn(ITEM_ID_1);

		testObj.storeItem(batchJobItemMock1);

		verify(cacheMock).put(ITEM_ID_1, batchJobItemMock1);
	}

	@Test
	void retrieveItem1_ShouldReturnItemFromCache_WhenIsPresent() {

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass()))
				.thenReturn(batchJobItemMock1);

		final Optional<BatchJobItem<Object>> result = testObj
				.retrieveItem((Class<BatchJobItem<Object>>) batchJobItemMock1.getClass(), ITEM_TYPE, ITEM_ID_1);

		assertThat(result).contains(batchJobItemMock1);
	}

	@Test
	void retrieveItem1_ShouldNotReturnItemFromCache_WhenIsNotPresent() {

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass())).thenReturn(null);

		final Optional<BatchJobItem<Object>> result = testObj
				.retrieveItem((Class<BatchJobItem<Object>>) batchJobItemMock1.getClass(), ITEM_TYPE, ITEM_ID_1);

		assertThat(result).isEmpty();
	}

	@Test
	void retrieveItem2_ShouldReturnItemFromCache_WhenIsPresent() {

		final BatchJobFailedItem batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setId(ITEM_ID_1);
		batchJobFailedItem.setType(ITEM_TYPE);

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass()))
				.thenReturn(batchJobItemMock1);

		final Optional<BatchJobItem<Object>> result = testObj
				.retrieveItem((Class<BatchJobItem<Object>>) batchJobItemMock1.getClass(), batchJobFailedItem);

		assertThat(result).contains(batchJobItemMock1);
	}

	@Test
	void retrieveItem2_ShouldNotReturnItemFromCache_WhenIsNotPresent() {

		final BatchJobFailedItem batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setId(ITEM_ID_1);
		batchJobFailedItem.setType(ITEM_TYPE);

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass())).thenReturn(null);

		final Optional<BatchJobItem<Object>> result = testObj
				.retrieveItem((Class<BatchJobItem<Object>>) batchJobItemMock1.getClass(), batchJobFailedItem);

		assertThat(result).isEmpty();
	}

	@Test
	void removeItem_ShouldRemoveItemFromCache() {

		testObj.removeItem(ITEM_TYPE, ITEM_ID_1);

		verify(cacheMock).evictIfPresent(ITEM_ID_1);
	}

	@Test
	void refreshCachedItems_ShouldRefreshItemFromCache_WhenTheyArePresent() {

		when(batchJobItemMock1.getItemType()).thenReturn(ITEM_TYPE);
		when(batchJobItemMock1.getItemId()).thenReturn(ITEM_ID_1);

		when(batchJobItemMock2.getItemType()).thenReturn(ITEM_TYPE);
		when(batchJobItemMock2.getItemId()).thenReturn(ITEM_ID_2);

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass()))
				.thenReturn(batchJobItemMock1);
		when(cacheMock.get(ITEM_ID_2, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass())).thenReturn(null);

		testObj.refreshCachedItems(List.of(batchJobItemMock1, batchJobItemMock2));

		verify(cacheMock).put(ITEM_ID_1, batchJobItemMock1);
		verify(cacheMock, never()).put(ITEM_ID_2, batchJobItemMock2);
	}

	@Test
	void refreshCachedItem_ShouldRefreshItemFromCache_WhenIsPresent() {

		when(batchJobItemMock1.getItemType()).thenReturn(ITEM_TYPE);
		when(batchJobItemMock1.getItemId()).thenReturn(ITEM_ID_1);

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass()))
				.thenReturn(batchJobItemMock1);

		testObj.refreshCachedItem(batchJobItemMock1);

		verify(cacheMock).put(ITEM_ID_1, batchJobItemMock1);
	}

	@Test
	void refreshCachedItem_ShouldNotRefreshItemFromCache_WhenIsNotPresent() {

		when(batchJobItemMock1.getItemType()).thenReturn(ITEM_TYPE);
		when(batchJobItemMock1.getItemId()).thenReturn(ITEM_ID_1);

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass())).thenReturn(null);

		testObj.refreshCachedItem(batchJobItemMock1);

		verify(cacheMock, never()).put(ITEM_ID_1, batchJobItemMock1);
	}

	@Test
	void retrieveAllItems1_ShouldRetrieveAllItemsFromCacheInAMap() {

		final BatchJobFailedItem batchJobFailedItem1 = new BatchJobFailedItem();
		batchJobFailedItem1.setType(ITEM_TYPE);
		batchJobFailedItem1.setId(ITEM_ID_1);

		final BatchJobFailedItem batchJobFailedItem2 = new BatchJobFailedItem();
		batchJobFailedItem2.setType(ITEM_TYPE);
		batchJobFailedItem2.setId(ITEM_ID_2);

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass()))
				.thenReturn(batchJobItemMock1);
		when(cacheMock.get(ITEM_ID_2, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass()))
				.thenReturn(batchJobItemMock2);

		final Map<BatchJobFailedItem, Optional<BatchJobItem<Object>>> result = testObj.retrieveAllItems(
				(Class<BatchJobItem<Object>>) batchJobItemMock1.getClass(),
				List.of(batchJobFailedItem1, batchJobFailedItem2));

		assertThat(result).containsEntry(batchJobFailedItem1, Optional.of(batchJobItemMock1))
				.containsEntry(batchJobFailedItem2, Optional.of(batchJobItemMock2));
	}

	@Test
	void retrieveAllItems2_ShouldRetrieveAllItemsFromCacheInAMapByTheGivenResolver() {

		final BatchJobFailedItem batchJobFailedItem1 = new BatchJobFailedItem();
		batchJobFailedItem1.setType(ITEM_TYPE);
		batchJobFailedItem1.setId(ITEM_ID_1);

		final BatchJobFailedItem batchJobFailedItem2 = new BatchJobFailedItem();
		batchJobFailedItem2.setType(ITEM_TYPE);
		batchJobFailedItem2.setId(ITEM_ID_2);

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass()))
				.thenReturn(batchJobItemMock1);
		when(cacheMock.get(ITEM_ID_2, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass())).thenReturn(null)
				.thenReturn(batchJobItemMock2);

		when(batchJobItemMock2.getItemType()).thenReturn(ITEM_TYPE);
		when(batchJobItemMock2.getItemId()).thenReturn(ITEM_ID_2);

		final Map<BatchJobFailedItem, Optional<BatchJobItem<Object>>> result = testObj.retrieveAllItems(
				(Class<BatchJobItem<Object>>) batchJobItemMock1.getClass(),
				List.of(batchJobFailedItem1, batchJobFailedItem2), batchJobFailedItems -> List.of(batchJobItemMock2));

		verify(cacheMock).put(ITEM_ID_2, batchJobItemMock2);
		verify(cacheMock, never()).put(ITEM_ID_1, batchJobItemMock1);

		assertThat(result).containsEntry(batchJobFailedItem1, Optional.of(batchJobItemMock1))
				.containsEntry(batchJobFailedItem2, Optional.of(batchJobItemMock2));
	}

	@Test
	void retrieveAllItems3_ShouldRetrieveAllItemsFromCacheInAMapByTheGivenResolverAndTheGivenPolicy() {

		when(batchJobItemMock1.getItemType()).thenReturn(ITEM_TYPE);
		when(batchJobItemMock1.getItemId()).thenReturn(ITEM_ID_1);

		when(batchJobItemMock2.getItemType()).thenReturn(ITEM_TYPE);
		when(batchJobItemMock2.getItemId()).thenReturn(ITEM_ID_2);

		final BatchJobFailedItem batchJobFailedItem1 = new BatchJobFailedItem();
		batchJobFailedItem1.setType(ITEM_TYPE);
		batchJobFailedItem1.setId(ITEM_ID_1);

		final BatchJobFailedItem batchJobFailedItem2 = new BatchJobFailedItem();
		batchJobFailedItem2.setType(ITEM_TYPE);
		batchJobFailedItem2.setId(ITEM_ID_2);

		when(cacheMock.get(ITEM_ID_1, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass()))
				.thenReturn(batchJobItemMock1);
		when(cacheMock.get(ITEM_ID_2, (Class<BatchJobItem<Object>>) batchJobItemMock1.getClass())).thenReturn(null)
				.thenReturn(batchJobItemMock2);

		when(batchJobItemMock2.getItemType()).thenReturn(ITEM_TYPE);
		when(batchJobItemMock2.getItemId()).thenReturn(ITEM_ID_2);

		final Map<BatchJobFailedItem, Optional<BatchJobItem<Object>>> result = testObj.retrieveAllItems(
				(Class<BatchJobItem<Object>>) batchJobItemMock1.getClass(),
				List.of(batchJobFailedItem1, batchJobFailedItem2),
				batchJobFailedItems -> List.of(batchJobItemMock1, batchJobItemMock2),
				cacheFailures -> (List.of(batchJobFailedItem1, batchJobFailedItem2)));

		verify(cacheMock).put(ITEM_ID_2, batchJobItemMock2);
		verify(cacheMock).put(ITEM_ID_1, batchJobItemMock1);

		assertThat(result).containsEntry(batchJobFailedItem1, Optional.of(batchJobItemMock1))
				.containsEntry(batchJobFailedItem2, Optional.of(batchJobItemMock2));
	}

}
