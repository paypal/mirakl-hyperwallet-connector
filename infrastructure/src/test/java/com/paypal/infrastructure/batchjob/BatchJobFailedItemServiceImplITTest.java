package com.paypal.infrastructure.batchjob;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("IntegrationTest")
@SpringBootTest(classes = BatchJobFailedItemServiceImplITTestContext.class)
@TestPropertySource(
		locations = { "classpath:infrastructure-test.properties", "classpath:infrastructure-test-db.properties" })
@ExtendWith(SpringExtension.class)
class BatchJobFailedItemServiceImplITTest {

	@Autowired
	private BatchJobFailedItemService batchJobFailedItemService;

	/**
	 * For testing purpose setting up max failed item as five in
	 * infrastructure-test.properties -> retry.maxFailedItemsToProcessed = 5
	 */
	@Test
	void getFailedItemsForRetry_ShouldReturnNoMoreThanFiveFailedItems() {

		final MyItem failedItem1 = new MyItem("failedItem1");
		final MyItem failedItem2 = new MyItem("failedItem2");
		final MyItem failedItem3 = new MyItem("failedItem3");
		final MyItem failedItem4 = new MyItem("failedItem4");
		final MyItem failedItem5 = new MyItem("failedItem5");
		final MyItem failedItem6 = new MyItem("failedItem6");

		batchJobFailedItemService.saveItemFailed(failedItem1);
		batchJobFailedItemService.saveItemFailed(failedItem2);
		batchJobFailedItemService.saveItemFailed(failedItem3);
		batchJobFailedItemService.saveItemFailed(failedItem4);
		batchJobFailedItemService.saveItemFailed(failedItem5);
		batchJobFailedItemService.saveItemFailed(failedItem6);

		final List<BatchJobFailedItem> failedItemsForRetry = batchJobFailedItemService
				.getFailedItemsForRetry(MyItem.class.getSimpleName());

		assertThat(failedItemsForRetry).hasSize(5);
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

}
