package com.paypal.jobsystem.batchjobsupport.model;

import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AbstractBatchJobItemTest {

	private static final String ITEM = "Item";

	@Test
	void getItem_ShouldReturnTheItemAddedInTheConstructor() {

		final AbstractBatchJobItem<Object> testObj = new MyAbstractBatchJobItem(ITEM);

		Assertions.assertThat(testObj.getItem()).isEqualTo(ITEM);
	}

	private static class MyAbstractBatchJobItem extends AbstractBatchJobItem<Object> {

		protected MyAbstractBatchJobItem(final Object item) {
			super(item);
		}

		@Override
		public String getItemId() {
			return null;
		}

		@Override
		public String getItemType() {
			return null;
		}

	}

}
