package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.batchjob.BatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CreditNotesExtractBatchJobTest {

	@InjectMocks
	private CreditNotesExtractBatchJob testObj;

	@Mock
	private CreditNotesExtractBatchJobItemsExtractor creditNotesExtractBatchJobItemsExtractorMock;

	@Mock
	private CreditNotesExtractBatchJobItemProcessor creditNotesExtractBatchJobItemProcessorMock;

	@Test
	void getBatchJobItemsExtractor_ShouldReturnCreditNotesExtractBatchJobItemsExtractor() {

		final BatchJobItemsExtractor<BatchJobContext, CreditNoteExtractJobItem> result = testObj
				.getBatchJobItemsExtractor();

		assertThat(result).isEqualTo(creditNotesExtractBatchJobItemsExtractorMock);
	}

	@Test
	void getBatchJobItemProcessor_ShouldReturnCreditNotesExtractBatchJobItemProcessor() {

		final BatchJobItemProcessor<BatchJobContext, CreditNoteExtractJobItem> result = testObj
				.getBatchJobItemProcessor();

		assertThat(result).isEqualTo(creditNotesExtractBatchJobItemProcessorMock);
	}

	@Test
	void getBatchJobType_shouldReturnExtractType() {
		assertThat(testObj.getType()).isEqualTo(BatchJobType.EXTRACT);
	}

}
