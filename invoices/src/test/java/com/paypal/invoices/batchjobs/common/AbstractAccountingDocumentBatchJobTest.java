package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.*;
import com.paypal.invoices.batchjobs.invoices.InvoiceExtractJobItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AbstractAccountingDocumentBatchJobTest {

	@InjectMocks
	private MyAccountingDocumentBatchJob testObj;

	@Mock
	private AccountingDocumentBatchJobItemEnricher accountingDocumentBatchJobItemEnricherMock;

	@Mock
	private AccountingDocumentBatchJobPreProcessor accountingDocumentBatchJobPreProcessorMock;

	@Mock
	private AccountingDocumentBatchJobItemValidator accountingDocumentBatchJobItemValidatorMock;

	@SuppressWarnings("unchecked")
	@Test
	void getBatchJobItemValidator_shouldReturnAccountingDocumenHandler() {
		assertThat(testObj.getBatchJobItemValidator())
				.contains((BatchJobItemValidator) accountingDocumentBatchJobItemValidatorMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	void getBatchJobPreProcessor_shouldReturnAccountingDocumenHandler() {
		assertThat(testObj.getBatchJobPreProcessor())
				.contains((BatchJobPreProcessor) accountingDocumentBatchJobPreProcessorMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	void getBatchJobItemEnricher_shouldReturnAccountingDocumenHandler() {
		assertThat(testObj.getBatchJobItemEnricher())
				.contains((BatchJobItemEnricher) accountingDocumentBatchJobItemEnricherMock);
	}

	static class MyAccountingDocumentBatchJob extends AbstractAccountingDocumentBatchJob<InvoiceExtractJobItem> {

		protected MyAccountingDocumentBatchJob(
				AccountingDocumentBatchJobItemEnricher accountingDocumentBatchJobItemEnricher,
				AccountingDocumentBatchJobPreProcessor accountingDocumentBatchJobPreProcessor,
				AccountingDocumentBatchJobItemValidator accountingDocumentBatchJobItemValidator) {
			super(accountingDocumentBatchJobItemEnricher, accountingDocumentBatchJobPreProcessor,
					accountingDocumentBatchJobItemValidator);
		}

		@Override
		protected BatchJobItemProcessor<BatchJobContext, InvoiceExtractJobItem> getBatchJobItemProcessor() {
			return null;
		}

		@Override
		protected BatchJobItemsExtractor<BatchJobContext, InvoiceExtractJobItem> getBatchJobItemsExtractor() {
			return null;
		}

		@Override
		public BatchJobType getType() {
			return null;
		}

	}

}
