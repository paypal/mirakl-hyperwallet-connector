package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.util.TimeMachine;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Batch job failed item service.
 */
@Service
public class BatchJobFailedItemServiceImpl implements BatchJobFailedItemService {

	private final BatchJobFailedItemRepository failedItemRepository;

	public BatchJobFailedItemServiceImpl(final BatchJobFailedItemRepository failedItemRepository) {
		this.failedItemRepository = failedItemRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveItemFailed(final BatchJobItem<?> item) {
		var batchJobFailedItemId = new BatchJobFailedItemId(item.getItemId(), item.getItemType());
		var batchJobFailedItem = failedItemRepository.findById(batchJobFailedItemId).map(this::updateFailedItem)
				.orElse(newFailedItem(item));

		failedItemRepository.save(batchJobFailedItem);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeItemProcessed(final BatchJobItem<?> item) {
		var batchJobFailedItemId = new BatchJobFailedItemId(item.getItemId(), item.getItemType());
		failedItemRepository.findById(batchJobFailedItemId).ifPresent(failedItemRepository::delete);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BatchJobFailedItem> getFailedItemsForRetry(String itemType) {
		// TODO: Retrieve failed items applying retry policies
		return Collections.emptyList();
	}

	private BatchJobFailedItem newFailedItem(final BatchJobItem<?> item) {
		var batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setId(item.getItemId());
		batchJobFailedItem.setType(item.getItemType());
		batchJobFailedItem.setFirstFailureTimestamp(TimeMachine.now());
		batchJobFailedItem.setNumberOfRetries(0);

		return batchJobFailedItem;
	}

	private BatchJobFailedItem updateFailedItem(BatchJobFailedItem failedItem) {
		failedItem.setLastRetryTimestamp(TimeMachine.now());
		failedItem.setNumberOfRetries(failedItem.getNumberOfRetries() + 1);

		return failedItem;
	}

}
