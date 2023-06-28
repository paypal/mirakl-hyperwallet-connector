package com.paypal.jobsystem.batchjobfailures.services;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItemStatus;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobItemTrackInfoEntity;
import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItemId;
import com.paypal.jobsystem.batchjobfailures.services.retrypolicies.BatchJobFailedItemRetryPolicy;
import com.paypal.jobsystem.batchjobfailures.repositories.BatchJobFailedItemRepository;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

	@Value("${hmc.jobs.settings.retry-max-items}")
	private int maxNumberOfFailedItems;

	public BatchJobFailedItemServiceImpl(final BatchJobFailedItemRepository failedItemRepository,
			final BatchJobTrackingService batchJobTrackingService,
			final List<BatchJobFailedItemRetryPolicy> batchJobFailedItemRetryPolicies,
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
	public <T extends BatchJobItem<?>> void saveItemFailed(final T item) {

		final var batchJobFailedItemId = new BatchJobFailedItemId(item.getItemId(), item.getItemType());
		final var batchJobFailedItem = failedItemRepository.findById(batchJobFailedItemId).map(this::updateFailedItem)
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
						"Max retry attempts reached when processing item [%s], the item won't be automatically retried again. It will be processed again if the item has any changes or if a manual job execution is done.",
						batchJobFailedItem.getId()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends BatchJobItem<?>> void removeItemProcessed(final T item) {
		final var batchJobFailedItemId = new BatchJobFailedItemId(item.getItemId(), item.getItemType());
		failedItemRepository.findById(batchJobFailedItemId).ifPresent(failedItemRepository::delete);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BatchJobFailedItem> getFailedItemsForRetry(final String itemType) {
		final List<BatchJobFailedItem> failedItems = failedItemRepository
				.findByTypeAndStatusOrderByLastRetryTimestampAsc(itemType, BatchJobFailedItemStatus.RETRY_PENDING,
						Pageable.ofSize(getMaxNumberOfFailedItems()));

		final Set<String> itemsBeingProcessedIds = batchJobTrackingService
				.getItemsBeingProcessedOrEnquedToProcess(itemType).stream().map(BatchJobItemTrackInfoEntity::getItemId)
				.collect(Collectors.toSet());

		return failedItems.stream().filter(it -> !itemsBeingProcessedIds.contains(it.getId()))
				.filter(this::shouldRetryFailedItem).collect(Collectors.toList());
	}

	@Override
	public List<BatchJobFailedItem> getFailedItems(final String itemType) {
		return failedItemRepository.findByType(itemType);
	}

	@Override
	public List<BatchJobFailedItem> getFailedItems(final String itemType, final BatchJobFailedItemStatus status) {
		return failedItemRepository.findByTypeAndStatus(itemType, status);
	}

	@Override
	public <T extends BatchJobItem<?>> void checkUpdatedFailedItems(final Collection<T> extractedItems) {

		//@formatter:off
		extractedItems.stream()
				.map(batchJobItem -> new BatchJobFailedItemId(batchJobItem.getItemId(), batchJobItem.getItemType()))
				.map(failedItemRepository::findById)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.forEach(batchJobFailedItem -> {
					batchJobFailedItem.setNumberOfRetries(0);
					failedItemRepository.save(batchJobFailedItem);
				});
		//@formatter:on
	}

	private boolean shouldRetryFailedItem(final BatchJobFailedItem item) {
		return batchJobFailedItemRetryPolicies.stream().filter(it -> !it.shouldRetryFailedItem(item)).findAny()
				.isEmpty();
	}

	private <T extends BatchJobItem<?>> BatchJobFailedItem newFailedItem(final T item) {
		final var batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setId(item.getItemId());
		batchJobFailedItem.setType(item.getItemType());
		batchJobFailedItem.setFirstFailureTimestamp(TimeMachine.now());
		batchJobFailedItem.setNumberOfRetries(0);
		batchJobFailedItem.setStatus(BatchJobFailedItemStatus.RETRY_PENDING);

		return batchJobFailedItem;
	}

	private BatchJobFailedItem updateFailedItem(final BatchJobFailedItem failedItem) {
		failedItem.setLastRetryTimestamp(TimeMachine.now());
		failedItem.setNumberOfRetries(failedItem.getNumberOfRetries() + 1);

		return failedItem;
	}

	protected int getMaxNumberOfFailedItems() {
		return maxNumberOfFailedItems;
	}

}
