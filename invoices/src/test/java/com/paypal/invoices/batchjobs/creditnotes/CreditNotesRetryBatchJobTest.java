package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.BatchJobType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CreditNotesRetryBatchJobTest {

	@InjectMocks
	private CreditNotesRetryBatchJob testObj;

	@Mock
	private CreditNotesExtractBatchJobItemProcessor creditNotesExtractBatchJobItemProcessorMock;

	@Mock
	private CreditNotesRetryBatchJobItemsExtractor creditNotesRetryBatchJobItemsExtractorMock;

	@Test
	void getBatchJobItemProcessor_shouldReturnBatchJobItemProcessor() {
		assertThat(testObj.getBatchJobItemProcessor()).isEqualTo(creditNotesExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobItemsExtractor_shouldReturnRetryBatchJobItemExtractor() {
		assertThat(testObj.getBatchJobItemsExtractor()).isEqualTo(creditNotesRetryBatchJobItemsExtractorMock);
	}

	@Test
	void getBatchJobType_shouldReturnRetryType() {
		assertThat(testObj.getType()).isEqualTo(BatchJobType.RETRY);
	}

}
