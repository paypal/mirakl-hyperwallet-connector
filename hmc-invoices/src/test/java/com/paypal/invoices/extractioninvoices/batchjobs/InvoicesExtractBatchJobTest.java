package com.paypal.invoices.extractioninvoices.batchjobs;

import com.paypal.invoices.extractioninvoices.batchjobs.InvoiceExtractJobItem;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesExtractBatchJob;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesExtractBatchJobItemProcessor;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesExtractBatchJobItemsExtractor;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InvoicesExtractBatchJobTest {

	@InjectMocks
	private InvoicesExtractBatchJob testObj;

	@Mock
	private InvoicesExtractBatchJobItemsExtractor invoicesExtractBatchJobItemsExtractorMock;

	@Mock
	private InvoicesExtractBatchJobItemProcessor invoicesExtractBatchJobItemProcessorMock;

	@Test
	void getBatchJobItemsExtractor_ShouldReturnInvoicesExtractBatchJobItemsExtractor() {

		final BatchJobItemsExtractor<BatchJobContext, InvoiceExtractJobItem> result = testObj
				.getBatchJobItemsExtractor();

		assertThat(result).isEqualTo(invoicesExtractBatchJobItemsExtractorMock);
	}

	@Test
	void getBatchJobItemProcessor_ShouldReturnInvoicesExtractBatchJobItemProcessor() {

		final BatchJobItemProcessor<BatchJobContext, InvoiceExtractJobItem> result = testObj.getBatchJobItemProcessor();

		assertThat(result).isEqualTo(invoicesExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobType_shouldReturnExtractType() {
		assertThat(testObj.getType()).isEqualTo(BatchJobType.EXTRACT);
	}

}
