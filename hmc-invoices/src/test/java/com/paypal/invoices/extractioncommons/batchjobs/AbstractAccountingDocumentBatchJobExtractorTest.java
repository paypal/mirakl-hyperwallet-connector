package com.paypal.invoices.extractioncommons.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractAccountingDocumentBatchJobExtractorTest {

	private static final String INCLUDE_PAID_KEY = "includePaid";

	private static final Date DELTA = new Date();

	@InjectMocks
	@Spy
	private MyAbstractAccountingDocumentBatchJobExtractor testObj;

	@Mock
	private BatchJobTrackingService batchJobTrackingServiceMock;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_shouldCallGetItemsWithParamIncludePaidFalse_whenIncludePaidIsFalse() {
		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(INCLUDE_PAID_KEY))
				.thenReturn(false);

		testObj.getItems(batchJobContextMock, DELTA);

		verify(testObj).getItems(batchJobContextMock, DELTA, false);
	}

	@Test
	void getItems_shouldCallGetItemsWithParamIncludePaidTrue_whenIncludePaidIsTrue() {
		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(INCLUDE_PAID_KEY))
				.thenReturn(true);

		testObj.getItems(batchJobContextMock, DELTA);

		verify(testObj).getItems(batchJobContextMock, DELTA, true);
	}

	@Test
	void getItems_shouldCallGetItemsWithParamIncludePaidTrue_whenIncludePaidIsNull() {
		when(batchJobContextMock.getJobExecutionContext().getJobDetail().getJobDataMap().get(INCLUDE_PAID_KEY))
				.thenReturn(null);

		testObj.getItems(batchJobContextMock, DELTA);

		verify(testObj).getItems(batchJobContextMock, DELTA, false);
	}

	private static class MyAbstractAccountingDocumentBatchJobExtractor
			extends AbstractAccountingDocumentBatchJobExtractor<BatchJobContext, BatchJobItem<Object>> {

		protected MyAbstractAccountingDocumentBatchJobExtractor(final BatchJobTrackingService batchJobTrackingService) {
			super(batchJobTrackingService);
		}

		@Override
		protected Collection getItems(final BatchJobContext ctx, final Date delta, final boolean includePaid) {
			return null;
		}

	}

}
