package com.paypal.jobsystem.batchjobfailures.services.resolvepolicies;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjobfailures.services.resolvepolicies.AllRetryPendingFailedItemCacheFailureResolvePolicy;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItemStatus;
import org.assertj.core.api.Assertions;
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

		final List<BatchJobFailedItem> result = testObj.itemsToReloadOnCacheFailure(List.of(batchJobFailedItemMock1));

		assertThat(result).containsExactlyInAnyOrder(batchJobFailedItemMock1, batchJobFailedItemMock2);
	}

}
