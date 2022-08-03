package com.paypal.observability.batchjoblogging.service;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.observability.batchjoblogging.model.BatchJobLoggingTransaction;
import com.paypal.observability.loggingcontext.model.LoggingTransaction;
import com.paypal.observability.loggingcontext.service.LoggingContextService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchJobLoggingContextServiceImplTest {

	@InjectMocks
	private BatchJobLoggingContextServiceImpl testObj;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private LoggingContextService loggingContextServiceMock;

	@Captor
	private ArgumentCaptor<BatchJobLoggingTransaction> batchJobLoggingTransactionArgumentCaptor;

	@Test
	void refreshBatchJobInformation_shouldClearItemTypeAndItemIdAndSetAsSubTypeTheJobNameAndUpdateLoggingTransaction() {
		when(batchJobContextMock.getJobName()).thenReturn("jobName");
		final LoggingTransaction batchJobLoggingTransaction = new BatchJobLoggingTransaction("102230", "jobName");
		when(loggingContextServiceMock.getCurrentLoggingTransaction())
				.thenReturn(Optional.of(batchJobLoggingTransaction));

		testObj.refreshBatchJobInformation(batchJobContextMock);

		verify(loggingContextServiceMock).updateLoggingTransaction(batchJobLoggingTransactionArgumentCaptor.capture());
		final BatchJobLoggingTransaction expectedLoggingTransaction = batchJobLoggingTransactionArgumentCaptor
				.getValue();
		assertThat(expectedLoggingTransaction.getSubtype()).isEqualTo("jobName");
		assertThat(expectedLoggingTransaction.getId()).isEqualTo("102230");
		assertThat(expectedLoggingTransaction.getItemType()).isNull();
		assertThat(expectedLoggingTransaction.getItemId()).isNull();
	}

	@Test
	void testRefreshBatchJobInformation_shouldSetItemTypeAndItemIdWithItemInformationPassedAndUpdateLoggingTransaction() {
		final LoggingTransaction batchJobLoggingTransaction = new BatchJobLoggingTransaction("102230", "jobName");
		when(loggingContextServiceMock.getCurrentLoggingTransaction())
				.thenReturn(Optional.of(batchJobLoggingTransaction));
		final MyItem myItem = new MyItem("The item");

		testObj.refreshBatchJobInformation(batchJobContextMock, myItem);

		verify(loggingContextServiceMock).updateLoggingTransaction(batchJobLoggingTransactionArgumentCaptor.capture());
		final BatchJobLoggingTransaction expectedLoggingTransaction = batchJobLoggingTransactionArgumentCaptor
				.getValue();
		assertThat(expectedLoggingTransaction.getId()).isEqualTo("102230");
		assertThat(expectedLoggingTransaction.getItemType()).isEqualTo("MyItem");
		assertThat(expectedLoggingTransaction.getItemId()).isEqualTo("The item");
	}

	@Test
	void removeBatchJobItemInformation_shouldClearItemTypeAndItemIdInformation() {
		final LoggingTransaction batchJobLoggingTransaction = new BatchJobLoggingTransaction("102230", "jobName");
		when(loggingContextServiceMock.getCurrentLoggingTransaction())
				.thenReturn(Optional.of(batchJobLoggingTransaction));

		testObj.removeBatchJobItemInformation();

		verify(loggingContextServiceMock).updateLoggingTransaction(batchJobLoggingTransactionArgumentCaptor.capture());
		final BatchJobLoggingTransaction expectedLoggingTransaction = batchJobLoggingTransactionArgumentCaptor
				.getValue();
		assertThat(expectedLoggingTransaction.getItemType()).isNull();
		assertThat(expectedLoggingTransaction.getItemId()).isNull();
	}

	@Test
	void removeBatchJobInformation_shouldCallCloseLoggingTransaction() {
		testObj.removeBatchJobInformation();

		verify(loggingContextServiceMock).closeLoggingTransaction();
	}

	private static class MyItem extends AbstractBatchJobItem<String> {

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
