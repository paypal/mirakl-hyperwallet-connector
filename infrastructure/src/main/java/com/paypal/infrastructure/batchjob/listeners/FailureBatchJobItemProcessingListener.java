package com.paypal.infrastructure.batchjob.listeners;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobFailedItemService;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobType;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Failure batch job processing listener.
 */
@Component
public class FailureBatchJobItemProcessingListener extends AbstractBatchJobProcessingListenerSupport {

	private final BatchJobFailedItemService batchJobFailedItemService;

	public FailureBatchJobItemProcessingListener(BatchJobFailedItemService batchJobFailedItemService) {
		this.batchJobFailedItemService = batchJobFailedItemService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingFailure(BatchJobContext ctx, BatchJobItem<?> item, Exception e) {
		batchJobFailedItemService.saveItemFailed(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemProcessingSuccess(BatchJobContext ctx, BatchJobItem<?> item) {
		batchJobFailedItemService.removeItemProcessed(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemExtractionSuccessful(BatchJobContext ctx, Collection<BatchJobItem<?>> extractedItems) {
		if (BatchJobType.EXTRACT.equals(ctx.getBatchJob().getType())) {
			batchJobFailedItemService.checkUpdatedFailedItems(extractedItems);
		}
	}

}
