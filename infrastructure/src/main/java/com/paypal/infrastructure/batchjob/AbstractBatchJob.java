package com.paypal.infrastructure.batchjob;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Optional;

/**
 * Base class for all batch jobs. Managed the job execution main flow including batch
 * items retrieval, item processing, reporting and failure handling.
 */
@Slf4j
public abstract class AbstractBatchJob<C extends BatchJobContext, T extends BatchJobItem<?>> implements BatchJob<C, T> {

	protected abstract BatchJobItemProcessor<C, T> getBatchJobItemProcessor();

	protected abstract BatchJobItemsExtractor<C, T> getBatchJobItemsExtractor();

	protected Optional<BatchJobItemValidator<C, T>> getBatchJobItemValidator() {
		return Optional.empty();
	}

	protected Optional<BatchJobPreProcessor<C, T>> getBatchJobPreProcessor() {
		return Optional.empty();
	}

	protected Optional<BatchJobItemEnricher<C, T>> getBatchJobItemEnricher() {
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<T> getItems(C ctx) {
		return getBatchJobItemsExtractor().getItems(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processItem(final C ctx, final T jobItem) {
		getBatchJobItemProcessor().processItem(ctx, jobItem);
	}

	@Override
	public void prepareForItemProcessing(C ctx, Collection<T> itemsToBeProcessed) {
		getBatchJobPreProcessor().ifPresent(it -> it.prepareForProcessing(ctx, itemsToBeProcessed));
	}

	@Override
	public T enrichItem(C ctx, T jobItem) {
		BatchJobItemEnricher<C, T> batchJobItemEnricher = getBatchJobItemEnricher().orElse(null);
		return batchJobItemEnricher != null ? batchJobItemEnricher.enrichItem(ctx, jobItem) : jobItem;
	}

	@Override
	public BatchJobItemValidationResult validateItem(C ctx, T jobItem) {
		BatchJobItemValidator<C, T> batchJobItemValidator = getBatchJobItemValidator().orElse(null);
		return batchJobItemValidator != null ? batchJobItemValidator.validateItem(ctx, jobItem)
				: buildValidValidationResult();
	}

	private BatchJobItemValidationResult buildValidValidationResult() {
		return BatchJobItemValidationResult.builder().status(BatchJobItemValidationStatus.VALID).build();
	}

}
