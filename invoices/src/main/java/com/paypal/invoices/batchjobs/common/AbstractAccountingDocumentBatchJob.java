package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.*;

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
