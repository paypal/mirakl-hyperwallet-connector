package com.paypal.invoices.batchjobs.invoices;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
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

}
