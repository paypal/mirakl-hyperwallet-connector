package com.paypal.infrastructure.batchjob.integrationtests;

import com.paypal.infrastructure.batchjob.BatchJobFailedItemId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("IntegrationTest")
@SpringBootTest(classes = BatchJobTestContext.class)
@TestPropertySource(
		locations = { "classpath:infrastructure-test.properties", "classpath:infrastructure-test-db.properties" })
@ExtendWith(SpringExtension.class)
class BatchJobFailedItemCacheTestContextITTest extends AbstractBatchJobTestSupport {

	@Test
	void shouldAddFailedItemsToCache() {
		TestBatchJobItem testBatchJobFailedItem = new TestBatchJobItem("id1", "val1");
		batchJobFailedItemService.saveItemFailed(testBatchJobFailedItem);

		assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "id1")).isPresent();

		assertThat(batchJobFailedItemRepository.findById(
				new BatchJobFailedItemId(testBatchJobFailedItem.getItemId(), testBatchJobFailedItem.getItemType())))
						.isPresent();
	}

	@Test
	void shouldRemoveSuccessfullItemsFromCache() {
		TestBatchJobItem testBatchJobFailedItem = new TestBatchJobItem("id1", "val1");
		batchJobFailedItemService.saveItemFailed(testBatchJobFailedItem);
		batchJobFailedItemService.removeItemProcessed(testBatchJobFailedItem);

		assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "id1")).isEmpty();
	}

	@Test
	void shouldRefreshCachedItems() {
		TestBatchJobItem testBatchJobFailedItem = new TestBatchJobItem("id1", "val1");
		TestBatchJobItem testBatchJobFailedItemUpdated = new TestBatchJobItem("id1", "val2");

		batchJobFailedItemService.saveItemFailed(testBatchJobFailedItem);
		batchJobFailedItemService.checkUpdatedFailedItems(List.of(testBatchJobFailedItemUpdated));

		assertThat(batchJobFailedItemCacheService
				.retrieveItem(TestBatchJobItem.class, TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "id1").get().getItem())
						.isEqualTo("val2");
	}

	@Test
	void shouldNotRefreshNotCachedItems() {
		TestBatchJobItem testBatchJobFailedItemUpdated = new TestBatchJobItem("id1", "val2");

		batchJobFailedItemService.checkUpdatedFailedItems(List.of(testBatchJobFailedItemUpdated));

		assertThat(batchJobFailedItemCacheService.retrieveItem(TestBatchJobItem.class,
				TestBatchJobItem.TEST_BATCH_JOB_ITEM_TYPE, "id1")).isEmpty();
	}

}
