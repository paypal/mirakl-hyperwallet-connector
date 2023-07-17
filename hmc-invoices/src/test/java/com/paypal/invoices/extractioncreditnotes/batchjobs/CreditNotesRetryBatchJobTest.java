package com.paypal.invoices.extractioncreditnotes.batchjobs;

import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesExtractBatchJobItemProcessor;
import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesRetryBatchJob;
import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesRetryBatchJobItemsExtractor;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
