package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackInfoEntity;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.TimeMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Batch job failed item service.
 */
@Transactional
@Service
public class BatchJobFailedItemServiceImpl implements BatchJobFailedItemService {

	private final BatchJobFailedItemRepository failedItemRepository;

	private final BatchJobTrackingService batchJobTrackingService;

	private final List<BatchJobFailedItemRetryPolicy> batchJobFailedItemRetryPolicies;

	private final MailNotificationUtil mailNotificationUtil;

	public BatchJobFailedItemServiceImpl(final BatchJobFailedItemRepository failedItemRepository,
			BatchJobTrackingService batchJobTrackingService,
			List<BatchJobFailedItemRetryPolicy> batchJobFailedItemRetryPolicies,
			final MailNotificationUtil mailNotificationUtil) {
		this.failedItemRepository = failedItemRepository;
		this.batchJobTrackingService = batchJobTrackingService;
		this.batchJobFailedItemRetryPolicies = batchJobFailedItemRetryPolicies;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveItemFailed(final BatchJobItem<?> item) {

		var batchJobFailedItemId = new BatchJobFailedItemId(item.getItemId(), item.getItemType());
		var batchJobFailedItem = failedItemRepository.findById(batchJobFailedItemId).map(this::updateFailedItem)
				.orElseGet(() -> newFailedItem(item));

		checkMaxAttempts(batchJobFailedItem);

		failedItemRepository.save(batchJobFailedItem);
	}

	private void checkMaxAttempts(final BatchJobFailedItem batchJobFailedItem) {

		if (!BatchJobFailedItemStatus.RETRIES_EXHAUSTED.equals(batchJobFailedItem.getStatus())
				&& batchJobFailedItem.getNumberOfRetries() >= MAX_ATTEMPTS) {

			setBatchJobItemExhaustedStatus(batchJobFailedItem);

			sendNotificationEmail(batchJobFailedItem);
		}
	}

	private void setBatchJobItemExhaustedStatus(final BatchJobFailedItem batchJobFailedItem) {

		batchJobFailedItem.setStatus(BatchJobFailedItemStatus.RETRIES_EXHAUSTED);
	}

	private void sendNotificationEmail(final BatchJobFailedItem batchJobFailedItem) {

		mailNotificationUtil.sendPlainTextEmail(
				String.format("Max retry attempts reached when processing item [%s]", batchJobFailedItem.getId()),
				String.format(
						"Max retry attempts reached when processing item [%s], the item won't be processed anymore",
						batchJobFailedItem.getId()));
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
		List<BatchJobFailedItem> failedItems = failedItemRepository.findByTypeAndStatus(itemType,
				BatchJobFailedItemStatus.RETRY_PENDING);

		Set<String> itemsBeingProcessedIds = batchJobTrackingService.getItemsBeingProcessedOrEnquedToProcess(itemType)
				.stream().map(BatchJobItemTrackInfoEntity::getItemId).collect(Collectors.toSet());

		return failedItems.stream().filter(it -> !itemsBeingProcessedIds.contains(it.getId()))
				.filter(this::shouldRetryFailedItem).collect(Collectors.toList());
	}

	@Override
	public List<BatchJobFailedItem> getFailedItems(String itemType) {
		return failedItemRepository.findByType(itemType);
	}

	private boolean shouldRetryFailedItem(BatchJobFailedItem item) {
		return batchJobFailedItemRetryPolicies.stream().filter(it -> !it.shouldRetryFailedItem(item)).findAny()
				.isEmpty();
	}

	private BatchJobFailedItem newFailedItem(final BatchJobItem<?> item) {
		var batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setId(item.getItemId());
		batchJobFailedItem.setType(item.getItemType());
		batchJobFailedItem.setFirstFailureTimestamp(TimeMachine.now());
		batchJobFailedItem.setNumberOfRetries(0);
		batchJobFailedItem.setStatus(BatchJobFailedItemStatus.RETRY_PENDING);

		return batchJobFailedItem;
	}

	private BatchJobFailedItem updateFailedItem(BatchJobFailedItem failedItem) {
		failedItem.setLastRetryTimestamp(TimeMachine.now());
		failedItem.setNumberOfRetries(failedItem.getNumberOfRetries() + 1);

		return failedItem;
	}

}
