package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchJobFailedItemServiceImplTest {

	private static final String ID_001 = "001";

	private static final String SELLER_TYPE = "SELLER";

	private static final int INITIAL_NUMBER_OF_RETRIES = 3;

	@InjectMocks
	private BatchJobFailedItemServiceImpl testObj;

	@Mock
	private BatchJobFailedItemRepository batchJobFailedItemRepositoryMock;

	@Captor
	private ArgumentCaptor<BatchJobFailedItem> batchJobFailedItemArgumentCaptor;

	@Test
	void itemFailed_ShouldCreateAndSaveAnewFailedItem_WhenBatchJobItemIsNotFound() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		final MySellerBatchJobItem sellerBatchJobItem = new MySellerBatchJobItem(new Object());
		final BatchJobFailedItemId seller = new BatchJobFailedItemId(ID_001, SELLER_TYPE);
		when(batchJobFailedItemRepositoryMock.findById(seller)).thenReturn(Optional.empty());

		testObj.saveItemFailed(sellerBatchJobItem);

		verify(batchJobFailedItemRepositoryMock).save(batchJobFailedItemArgumentCaptor.capture());
		final BatchJobFailedItem capturedBatchJobFailedItem = batchJobFailedItemArgumentCaptor.getValue();

		assertThat(capturedBatchJobFailedItem.getId()).isEqualTo(ID_001);
		assertThat(capturedBatchJobFailedItem.getType()).isEqualTo(SELLER_TYPE);
		assertThat(capturedBatchJobFailedItem.getNumberOfRetries()).isZero();
		assertThat(capturedBatchJobFailedItem.getFirstFailureTimestamp()).isEqualTo(now);
	}

	@Test
	void itemFailed_ShouldUpdateAndSaveTheFailedItem_WhenBatchJobItemIsFound() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		final MySellerBatchJobItem sellerBatchJobItem = new MySellerBatchJobItem(new Object());
		final BatchJobFailedItemId seller = new BatchJobFailedItemId(ID_001, SELLER_TYPE);
		final BatchJobFailedItem batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setId(ID_001);
		batchJobFailedItem.setType(SELLER_TYPE);
		batchJobFailedItem.setNumberOfRetries(INITIAL_NUMBER_OF_RETRIES);
		when(batchJobFailedItemRepositoryMock.findById(seller)).thenReturn(Optional.of(batchJobFailedItem));

		testObj.saveItemFailed(sellerBatchJobItem);

		verify(batchJobFailedItemRepositoryMock).save(batchJobFailedItemArgumentCaptor.capture());
		final BatchJobFailedItem capturedBatchJobFailedItem = batchJobFailedItemArgumentCaptor.getValue();

		assertThat(capturedBatchJobFailedItem.getId()).isEqualTo(ID_001);
		assertThat(capturedBatchJobFailedItem.getType()).isEqualTo(SELLER_TYPE);
		assertThat(capturedBatchJobFailedItem.getNumberOfRetries()).isEqualTo(INITIAL_NUMBER_OF_RETRIES + 1);
		assertThat(capturedBatchJobFailedItem.getLastRetryTimestamp()).isEqualTo(now);
	}

	@Test
	void itemProcessed_ShouldShouldRemoveTheFailedItem_WhenFailedItemIsFound() {

		final MySellerBatchJobItem sellerBatchJobItem = new MySellerBatchJobItem(new Object());
		final BatchJobFailedItemId seller = new BatchJobFailedItemId(ID_001, SELLER_TYPE);
		final BatchJobFailedItem batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setId(ID_001);
		batchJobFailedItem.setType(SELLER_TYPE);
		batchJobFailedItem.setNumberOfRetries(INITIAL_NUMBER_OF_RETRIES);
		when(batchJobFailedItemRepositoryMock.findById(seller)).thenReturn(Optional.of(batchJobFailedItem));

		testObj.removeItemProcessed(sellerBatchJobItem);

		verify(batchJobFailedItemRepositoryMock).delete(batchJobFailedItem);
	}

	@Test
	void itemProcessed_ShouldShouldNitRemoveTheFailedItem_WhenFailedItemIsNotFound() {

		final MySellerBatchJobItem sellerBatchJobItem = new MySellerBatchJobItem(new Object());
		final BatchJobFailedItemId seller = new BatchJobFailedItemId(ID_001, SELLER_TYPE);
		when(batchJobFailedItemRepositoryMock.findById(seller)).thenReturn(Optional.empty());

		testObj.removeItemProcessed(sellerBatchJobItem);

		verify(batchJobFailedItemRepositoryMock, never()).delete(any());
	}

	@Test
	void getFailedItemsForRetry() {

		// TODO: Retrieve failed items applying retry policies
		assertThat(testObj.getFailedItemsForRetry(SELLER_TYPE)).isEmpty();
	}

	private static class MySellerBatchJobItem extends AbstractBatchJobItem<Object> {

		protected MySellerBatchJobItem(final Object item) {
			super(item);
		}

		@Override
		public String getItemId() {
			return ID_001;
		}

		@Override
		public String getItemType() {
			return SELLER_TYPE;
		}

	}

}
