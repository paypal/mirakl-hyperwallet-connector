package com.paypal.infrastructure.batchjob.cache;

import com.paypal.infrastructure.batchjob.BatchJobFailedItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnlyCacheFailureItemsFailedItemCacheFailureResolvePolicyTest {

	@InjectMocks
	private OnlyCacheFailureItemsFailedItemCacheFailureResolvePolicy testObj;

	@Mock
	private BatchJobFailedItem batchJobFailedItemMock1, batchJobFailedItemMock2;

	@Test
	void itemsToReload_shouldReturnFailedItems() {
		when(batchJobFailedItemMock1.getType()).thenReturn("itemType");
		when(batchJobFailedItemMock2.getType()).thenReturn("itemType");

		List<BatchJobFailedItem> result = testObj
				.itemsToReloadOnCacheFailure(List.of(batchJobFailedItemMock1, batchJobFailedItemMock2));

		assertThat(result).containsExactlyInAnyOrder(batchJobFailedItemMock1, batchJobFailedItemMock2);
	}

}
