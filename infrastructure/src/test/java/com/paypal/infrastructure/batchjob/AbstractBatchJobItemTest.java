package com.paypal.infrastructure.batchjob;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

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
