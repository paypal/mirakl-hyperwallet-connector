package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackInfoEntity;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchJobFailedItemServiceImplTest {

	private static final String ID_001 = "001";

	private static final String ID_002 = "002";

	private static final String SELLER_TYPE = "SELLER";

	private static final int INITIAL_NUMBER_OF_RETRIES = 3;

	private BatchJobFailedItemServiceImpl testObj;

	@Mock
	private BatchJobFailedItemRepository batchJobFailedItemRepositoryMock;

	@Mock
	private BatchJobTrackingService batchJobTrackingServiceMock;

	@Mock
	private BatchJobFailedItemRetryPolicy batchJobFailedItemRetryPolicyMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Captor
	private ArgumentCaptor<BatchJobFailedItem> batchJobFailedItemArgumentCaptor;

	@Mock
	private BatchJobFailedItem batchJobFailedItem1Mock, batchJobFailedItem2Mock, batchJobFailedItem3Mock;

	@Mock
	private BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity1Mock;

	@Mock
	private BatchJobItem<?> batchJobItem1Mock, batchJobItem2Mock, batchJobItem3Mock;

	@BeforeEach
	void setUp() {

		testObj = spy(new BatchJobFailedItemServiceImpl(batchJobFailedItemRepositoryMock, batchJobTrackingServiceMock,
				List.of(batchJobFailedItemRetryPolicyMock), mailNotificationUtilMock));
	}

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
	void itemFailed_ShouldChangeItemStatusToExhaustedAndSendEmail_WhenBatchJobItemNumberOfRetriesIsGreaterOrEqualsThanFive() {

		final MySellerBatchJobItem sellerBatchJobItem = new MySellerBatchJobItem(new Object());
		final BatchJobFailedItemId seller = new BatchJobFailedItemId(ID_001, SELLER_TYPE);
		when(batchJobFailedItemRepositoryMock.findById(seller)).thenReturn(Optional.of(batchJobFailedItem1Mock));

		when(batchJobFailedItem1Mock.getStatus()).thenReturn(BatchJobFailedItemStatus.RETRY_PENDING);
		when(batchJobFailedItem1Mock.getNumberOfRetries()).thenReturn(5);
		when(batchJobFailedItem1Mock.getId()).thenReturn(ID_001);

		testObj.saveItemFailed(sellerBatchJobItem);

		verify(batchJobFailedItem1Mock).setStatus(BatchJobFailedItemStatus.RETRIES_EXHAUSTED);

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Max retry attempts reached when processing item [" + ID_001 + "]",
				"Max retry attempts reached when processing item [" + ID_001
						+ "], the item won't be processed anymore");
	}

	@Test
	void itemFailed_ShouldNotChangeItemStatusToExhaustedAndNotSendEmail_WhenBatchJobItemNumberOfRetriesIsLessThanFive() {

		final MySellerBatchJobItem sellerBatchJobItem = new MySellerBatchJobItem(new Object());
		final BatchJobFailedItemId seller = new BatchJobFailedItemId(ID_001, SELLER_TYPE);
		when(batchJobFailedItemRepositoryMock.findById(seller)).thenReturn(Optional.of(batchJobFailedItem1Mock));

		when(batchJobFailedItem1Mock.getStatus()).thenReturn(BatchJobFailedItemStatus.RETRY_PENDING);
		when(batchJobFailedItem1Mock.getNumberOfRetries()).thenReturn(4);

		testObj.saveItemFailed(sellerBatchJobItem);

		verify(batchJobFailedItem1Mock, never()).setStatus(BatchJobFailedItemStatus.RETRIES_EXHAUSTED);

		verify(mailNotificationUtilMock, never()).sendPlainTextEmail(
				"Max retry attempts reached when processing item [" + ID_001 + "]",
				"Max retry attempts reached when processing item [" + ID_001
						+ "], the item won't be processed anymore");
	}

	@Test
	void itemFailed_ShouldNotChangeItemStatusToExhaustedAndNotSendEmail_WhenBatchJobItemNumberOfRetriesIsGreaterOrEqualsThanFiveAndStatusIsRetriesExhausted() {

		final MySellerBatchJobItem sellerBatchJobItem = new MySellerBatchJobItem(new Object());
		final BatchJobFailedItemId seller = new BatchJobFailedItemId(ID_001, SELLER_TYPE);
		when(batchJobFailedItemRepositoryMock.findById(seller)).thenReturn(Optional.of(batchJobFailedItem1Mock));

		when(batchJobFailedItem1Mock.getStatus()).thenReturn(BatchJobFailedItemStatus.RETRIES_EXHAUSTED);
		when(batchJobFailedItem1Mock.getNumberOfRetries()).thenReturn(5);

		testObj.saveItemFailed(sellerBatchJobItem);

		verify(batchJobFailedItem1Mock, never()).setStatus(BatchJobFailedItemStatus.RETRIES_EXHAUSTED);

		verify(mailNotificationUtilMock, never()).sendPlainTextEmail(
				"Max retry attempts reached when processing item [" + ID_001 + "]",
				"Max retry attempts reached when processing item [" + ID_001
						+ "], the item won't be processed anymore");
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
	void getFailedItemsForRetry_ShouldReturnBatchJobFailedItemThatAreNotBeingProcessed() {

		doReturn(5).when(testObj).getMaxNumberOfFailedItems();

		when(batchJobFailedItem1Mock.getId()).thenReturn(ID_001);
		when(batchJobFailedItem2Mock.getId()).thenReturn(ID_002);

		when(batchJobFailedItemRetryPolicyMock.shouldRetryFailedItem(batchJobFailedItem2Mock)).thenReturn(true);

		when(batchJobFailedItemRepositoryMock.findByTypeAndStatusOrderByLastRetryTimestampAsc(SELLER_TYPE,
				BatchJobFailedItemStatus.RETRY_PENDING, Pageable.ofSize(5)))
						.thenReturn(List.of(batchJobFailedItem1Mock, batchJobFailedItem2Mock));

		when(batchJobItemTrackInfoEntity1Mock.getItemId()).thenReturn(ID_001);

		when(batchJobTrackingServiceMock.getItemsBeingProcessedOrEnquedToProcess(SELLER_TYPE))
				.thenReturn(List.of(batchJobItemTrackInfoEntity1Mock));

		final List<BatchJobFailedItem> result = testObj.getFailedItemsForRetry(SELLER_TYPE);

		assertThat(result.stream().map(BatchJobFailedItem::getId)).containsExactly(ID_002);
	}

	@Test
	void getFailedItems_ShouldReturnAllFailedItemsOfAType() {
		List<BatchJobFailedItem> batchJobFailedItems = List.of(batchJobFailedItem1Mock, batchJobFailedItem2Mock);
		when(batchJobFailedItemRepositoryMock.findByType("type1")).thenReturn(batchJobFailedItems);

		List<BatchJobFailedItem> result = testObj.getFailedItems("type1");

		assertThat(result).containsAll(batchJobFailedItems);
	}

	@Test
	void checkUpdatedFailedItems() {
		when(batchJobItem1Mock.getItemId()).thenReturn("1");
		when(batchJobItem1Mock.getItemType()).thenReturn("test");
		when(batchJobItem2Mock.getItemId()).thenReturn("2");
		when(batchJobItem2Mock.getItemType()).thenReturn("test");
		when(batchJobItem3Mock.getItemId()).thenReturn("3");
		when(batchJobItem3Mock.getItemType()).thenReturn("test");

		when(batchJobFailedItemRepositoryMock.findById(new BatchJobFailedItemId("1", "test")))
				.thenReturn(Optional.of(batchJobFailedItem1Mock));
		when(batchJobFailedItemRepositoryMock.findById(new BatchJobFailedItemId("2", "test")))
				.thenReturn(Optional.empty());
		when(batchJobFailedItemRepositoryMock.findById(new BatchJobFailedItemId("3", "test")))
				.thenReturn(Optional.of(batchJobFailedItem3Mock));

		testObj.checkUpdatedFailedItems(List.of(batchJobItem1Mock, batchJobItem2Mock, batchJobItem3Mock));

		verify(batchJobFailedItem1Mock).setNumberOfRetries(0);
		verify(batchJobFailedItemRepositoryMock).save(batchJobFailedItem1Mock);
		verify(batchJobFailedItemRepositoryMock, times(0)).save(batchJobFailedItem2Mock);
		verify(batchJobFailedItem3Mock).setNumberOfRetries(0);
		verify(batchJobFailedItemRepositoryMock).save(batchJobFailedItem3Mock);
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
