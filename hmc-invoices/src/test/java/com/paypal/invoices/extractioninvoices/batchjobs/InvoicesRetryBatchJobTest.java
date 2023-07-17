package com.paypal.invoices.extractioninvoices.batchjobs;

import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesExtractBatchJobItemProcessor;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesRetryBatchJob;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesRetryBatchJobItemsExtractor;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InvoicesRetryBatchJobTest {

	@InjectMocks
	private InvoicesRetryBatchJob testObj;

	@Mock
	private InvoicesRetryBatchJobItemsExtractor invoicesRetryBatchJobItemsExtractorMock;

	@Mock
	private InvoicesExtractBatchJobItemProcessor invoicesExtractBatchJobItemProcessorMock;

	@Test
	void getBatchJobItemProcessor_shouldReturnBatchJobItemProcessor() {
		assertThat(testObj.getBatchJobItemProcessor()).isEqualTo(invoicesExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_shouldReturnRetryBatchJobItemExtractor() {
		assertThat(testObj.getBatchJobItemsExtractor()).isEqualTo(invoicesRetryBatchJobItemsExtractorMock);
	}

	@Test
	void getBatchJobType_shouldReturnRetryType() {
		assertThat(testObj.getType()).isEqualTo(BatchJobType.RETRY);
	}

}
