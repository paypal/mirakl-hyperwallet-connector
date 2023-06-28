package com.paypal.jobsystem;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BatchJobFailedItemServiceImplITTest extends AbstractIntegrationTest {

	@Autowired
	private BatchJobFailedItemService batchJobFailedItemService;

	/**
	 * For testing purpose setting up max failed item as five in
	 * application-test.properties -> retry.maxFailedItemsToProcessed = 5
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

		protected MyItem(final String item) {
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
