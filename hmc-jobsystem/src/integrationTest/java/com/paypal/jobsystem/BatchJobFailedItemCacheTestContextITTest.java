package com.paypal.jobsystem;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItemId;
import com.paypal.jobsystem.testsupport.AbstractBatchJobTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class BatchJobFailedItemCacheTestContextITTest extends AbstractBatchJobTestSupport {

	@Test
	void shouldAddFailedItemsToCache() {
		final TestBatchJobItem testBatchJobFailedItem = new TestBatchJobItem("id1", "val1");
		batchJobFailedItemService.saveItemFailed(testBatchJobFailedItem);

		Assertions.assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "id1")).isPresent();

		Assertions.assertThat(batchJobFailedItemRepository.findById(
				new BatchJobFailedItemId(testBatchJobFailedItem.getItemId(), testBatchJobFailedItem.getItemType())))
				.isPresent();
	}

	@Test
	void shouldRemoveSuccessfullItemsFromCache() {
		final TestBatchJobItem testBatchJobFailedItem = new TestBatchJobItem("id1", "val1");
		batchJobFailedItemService.saveItemFailed(testBatchJobFailedItem);
		batchJobFailedItemService.removeItemProcessed(testBatchJobFailedItem);

		Assertions.assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "id1")).isEmpty();
	}

	@Test
	void shouldRefreshCachedItems() {
		final TestBatchJobItem testBatchJobFailedItem = new TestBatchJobItem("id1", "val1");
		final TestBatchJobItem testBatchJobFailedItemUpdated = new TestBatchJobItem("id1", "val2");

		batchJobFailedItemService.saveItemFailed(testBatchJobFailedItem);
		batchJobFailedItemService.checkUpdatedFailedItems(List.of(testBatchJobFailedItemUpdated));

		Assertions.assertThat(batchJobFailedItemCacheService
				.retrieveItem(TestBatchJobItem.class, TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "id1").get().getItem())
				.isEqualTo("val2");
	}

	@Test
	void shouldNotRefreshNotCachedItems() {
		final TestBatchJobItem testBatchJobFailedItemUpdated = new TestBatchJobItem("id1", "val2");

		batchJobFailedItemService.checkUpdatedFailedItems(List.of(testBatchJobFailedItemUpdated));

		Assertions.assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "id1")).isEmpty();
	}

}
