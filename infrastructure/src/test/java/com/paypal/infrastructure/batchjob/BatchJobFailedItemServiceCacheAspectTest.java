package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.cache.BatchJobFailedItemCacheService;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchJobFailedItemServiceCacheAspectTest {

	@InjectMocks
	private BatchJobFailedItemServiceCacheAspect testObj;

	@Mock
	private BatchJobFailedItemCacheService batchJobFailedItemCacheService;

	@Mock
	private JoinPoint joinPointMock;

	@Mock
	private BatchJobItem<Object> batchJobItemMock;

	@Test
	void beforeSaveItem_shouldCacheItem() {
		when(joinPointMock.getArgs()).thenReturn(new Object[] { batchJobItemMock });
		testObj.beforeSaveItem(joinPointMock);

		verify(batchJobFailedItemCacheService).storeItem(batchJobItemMock);
	}

	@Test
	void beforeRemoveItem_shouldRemoveItem() {
		when(joinPointMock.getArgs()).thenReturn(new Object[] { batchJobItemMock });
		when(batchJobItemMock.getItemId()).thenReturn("id");
		when(batchJobItemMock.getItemType()).thenReturn("type");
		testObj.beforeRemoveItem(joinPointMock);

		verify(batchJobFailedItemCacheService).removeItem("type", "id");
	}

	@Test
	void beforeCheckUpdatedItems() {
		when(joinPointMock.getArgs()).thenReturn(new Object[] { List.of(batchJobItemMock) });
		testObj.beforeCheckUpdatedItems(joinPointMock);

		verify(batchJobFailedItemCacheService)
				.refreshCachedItems(argThat((Collection c) -> c.contains(batchJobItemMock)));
	}

}
