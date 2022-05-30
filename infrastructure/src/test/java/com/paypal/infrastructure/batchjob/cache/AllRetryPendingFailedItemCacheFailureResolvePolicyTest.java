package com.paypal.infrastructure.batchjob.cache;

import com.paypal.infrastructure.batchjob.BatchJobFailedItem;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllRetryPendingFailedItemCacheFailureResolvePolicyTest {

	@InjectMocks
	private AllRetryPendingFailedItemCacheFailureResolvePolicy testObj;

	@Mock
	private BatchJobFailedItemService batchJobFailedItemService;

	@Mock
	private BatchJobFailedItem batchJobFailedItemMock1, batchJobFailedItemMock2;

	@Test
	void itemsToReload_shouldReturnAllRetryPendingItems() {
		when(batchJobFailedItemMock1.getType()).thenReturn("itemType");
		when(batchJobFailedItemService.getFailedItems("itemType", BatchJobFailedItemStatus.RETRY_PENDING))
				.thenReturn(List.of(batchJobFailedItemMock1, batchJobFailedItemMock2));

		List<BatchJobFailedItem> result = testObj.itemsToReloadOnCacheFailure(List.of(batchJobFailedItemMock1));

		assertThat(result).containsExactlyInAnyOrder(batchJobFailedItemMock1, batchJobFailedItemMock2);
	}

}
