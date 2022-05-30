package com.paypal.invoices.batchjobs.invoices;

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

}