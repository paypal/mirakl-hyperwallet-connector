package com.paypal.jobsystem.batchjobfailures.listeners;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.support.AbstractBatchJobProcessingListener;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobType;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Failure batch job processing listener.
 */
@Component
public class FailureBatchJobItemProcessingListener extends AbstractBatchJobProcessingListener {

	private final BatchJobFailedItemService batchJobFailedItemService;

	public FailureBatchJobItemProcessingListener(final BatchJobFailedItemService batchJobFailedItemService) {
		this.batchJobFailedItemService = batchJobFailedItemService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingFailure(final BatchJobContext ctx, final BatchJobItem<?> item, final Exception e) {
		batchJobFailedItemService.saveItemFailed(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingSuccess(final BatchJobContext ctx, final BatchJobItem<?> item) {
		batchJobFailedItemService.removeItemProcessed(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionSuccessful(final BatchJobContext ctx,
			final Collection<BatchJobItem<?>> extractedItems) {
		if (BatchJobType.EXTRACT.equals(ctx.getBatchJob().getType())) {
			batchJobFailedItemService.checkUpdatedFailedItems(extractedItems);
		}
	}

}
