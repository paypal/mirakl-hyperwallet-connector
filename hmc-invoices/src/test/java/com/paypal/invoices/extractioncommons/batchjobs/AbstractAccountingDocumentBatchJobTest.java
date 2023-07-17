package com.paypal.invoices.extractioncommons.batchjobs;

import com.paypal.invoices.extractioncommons.batchjobs.AbstractAccountingDocumentBatchJob;
import com.paypal.invoices.extractioncommons.batchjobs.AccountingDocumentBatchJobItemEnricher;
import com.paypal.invoices.extractioncommons.batchjobs.AccountingDocumentBatchJobItemValidator;
import com.paypal.invoices.extractioncommons.batchjobs.AccountingDocumentBatchJobPreProcessor;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoiceExtractJobItem;
import com.paypal.jobsystem.batchjobsupport.model.*;
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
				final AccountingDocumentBatchJobItemEnricher accountingDocumentBatchJobItemEnricher,
				final AccountingDocumentBatchJobPreProcessor accountingDocumentBatchJobPreProcessor,
				final AccountingDocumentBatchJobItemValidator accountingDocumentBatchJobItemValidator) {
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
