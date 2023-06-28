package com.paypal.jobsystem.batchjobfailures.services.resolvepolicies;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.infrastructure.support.exceptions.HMCException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractBatchJobFailedItemCacheFailureResolvePolicyTest {

	@InjectMocks
	private MyPolicy testObj;

	@Mock
	private BatchJobFailedItem batchJobFailedItemMock1, batchJobFailedItemMock2;

	@Test
	void itemsToReload_shouldReturnEmptyWhenBatchJobFailedItemsIsEmpty() {
		final List<BatchJobFailedItem> result = testObj.itemsToReloadOnCacheFailure(new ArrayList<>());

		assertThat(result).isEmpty();
	}

	@Test
	void itemsToReload_shouldThrowExceptionOnMultipleBatchJobItemTypes() {
		when(batchJobFailedItemMock1.getType()).thenReturn("t1");
		when(batchJobFailedItemMock2.getType()).thenReturn("t2");
		final List<BatchJobFailedItem> batchJobFailedItems = List.of(batchJobFailedItemMock1, batchJobFailedItemMock2);

		final Throwable throwable = catchThrowable(() -> testObj.itemsToReloadOnCacheFailure(batchJobFailedItems));

		assertThat(throwable).isNotNull().isInstanceOf(HMCException.class);
	}

	@Test
	void itemsToReload_shouldInvokeInternalMethodFromChildClasses() {
		final List<BatchJobFailedItem> batchJobFailedItems = List.of(batchJobFailedItemMock1, batchJobFailedItemMock2);

		final List<BatchJobFailedItem> result = testObj.itemsToReloadOnCacheFailure(batchJobFailedItems);

		assertThat(result).containsExactly(batchJobFailedItemMock2, batchJobFailedItemMock1);
	}

	static class MyPolicy extends AbstractBatchJobFailedItemCacheFailureResolvePolicy {

		@Override
		protected List<BatchJobFailedItem> itemsToReloadOnCacheFailureInternal(
				final List<BatchJobFailedItem> cacheFailures) {
			final List<BatchJobFailedItem> itemsToReload = new ArrayList<>(cacheFailures);
			Collections.reverse(itemsToReload);

			return itemsToReload;
		}

	}

}
