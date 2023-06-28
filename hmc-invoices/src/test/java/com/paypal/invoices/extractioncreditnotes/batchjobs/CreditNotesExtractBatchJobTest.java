package com.paypal.invoices.extractioncreditnotes.batchjobs;

import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNoteExtractJobItem;
import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesExtractBatchJob;
import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesExtractBatchJobItemProcessor;
import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesExtractBatchJobItemsExtractor;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemsExtractor;
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
