package com.paypal.invoices.extractioncommons.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemEnricher;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemValidator;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobPreProcessor;
import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJob;

import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class AbstractAccountingDocumentBatchJob<T extends AbstractAccountingDocumentBatchJobItem<?>>
		extends AbstractBatchJob<BatchJobContext, T> {

	private final AccountingDocumentBatchJobItemEnricher accountingDocumentBatchJobItemEnricher;

	private final AccountingDocumentBatchJobPreProcessor accountingDocumentBatchJobPreProcessor;

	private final AccountingDocumentBatchJobItemValidator accountingDocumentBatchJobItemValidator;

	protected AbstractAccountingDocumentBatchJob(
			final AccountingDocumentBatchJobItemEnricher accountingDocumentBatchJobItemEnricher,
			final AccountingDocumentBatchJobPreProcessor accountingDocumentBatchJobPreProcessor,
			final AccountingDocumentBatchJobItemValidator accountingDocumentBatchJobItemValidator) {
		this.accountingDocumentBatchJobItemEnricher = accountingDocumentBatchJobItemEnricher;
		this.accountingDocumentBatchJobPreProcessor = accountingDocumentBatchJobPreProcessor;
		this.accountingDocumentBatchJobItemValidator = accountingDocumentBatchJobItemValidator;
	}

	@Override
	protected Optional<BatchJobItemValidator<BatchJobContext, T>> getBatchJobItemValidator() {
		return Optional.of((BatchJobItemValidator) accountingDocumentBatchJobItemValidator);
	}

	@Override
	protected Optional<BatchJobPreProcessor<BatchJobContext, T>> getBatchJobPreProcessor() {
		return Optional.of((BatchJobPreProcessor) accountingDocumentBatchJobPreProcessor);
	}

	@Override
	protected Optional<BatchJobItemEnricher<BatchJobContext, T>> getBatchJobItemEnricher() {
		return Optional.of((BatchJobItemEnricher) accountingDocumentBatchJobItemEnricher);
	}

}
