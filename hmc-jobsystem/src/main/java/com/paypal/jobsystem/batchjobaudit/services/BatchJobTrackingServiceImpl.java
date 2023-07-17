package com.paypal.jobsystem.batchjobaudit.services;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjob.model.BatchJobItemStatus;
import com.paypal.jobsystem.batchjob.model.BatchJobStatus;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobItemTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobItemTrackingInfoId;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.repositories.BatchJobItemTrackingRepository;
import com.paypal.jobsystem.batchjobaudit.repositories.BatchJobTrackingRepository;
import com.paypal.infrastructure.support.date.TimeMachine;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BatchJobTrackingServiceImpl implements BatchJobTrackingService {

	protected static final Set<BatchJobItemStatus> ITEM_NOT_FINISHED_STATUSES = new HashSet<>(
			Arrays.asList(BatchJobItemStatus.IN_PROGRESS, BatchJobItemStatus.PENDING));

	protected static final Set<BatchJobItemStatus> ITEM_FINISHED_STATUSES = new HashSet<>(
			Arrays.asList(BatchJobItemStatus.SUCCESSFUL, BatchJobItemStatus.FAILED));

	protected static final Set<BatchJobStatus> JOB_NOT_FINISHED_STATUSES = new HashSet<>(
			Arrays.asList(BatchJobStatus.RUNNING, BatchJobStatus.NOT_STARTED));

	private final BatchJobTrackingRepository batchJobTrackingRepository;

	private final BatchJobItemTrackingRepository batchJobItemTrackingRepository;

	public BatchJobTrackingServiceImpl(final BatchJobTrackingRepository batchJobTrackingRepository,
			final BatchJobItemTrackingRepository batchJobItemTrackingRepository) {
		this.batchJobTrackingRepository = batchJobTrackingRepository;
		this.batchJobItemTrackingRepository = batchJobItemTrackingRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trackJobStart(final String batchJobId, final String batchJobType) {
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity = BatchJobTrackInfoEntity.builder().batchJobId(batchJobId)
				.batchJobType(batchJobType).startTime(TimeMachine.now()).status(BatchJobStatus.RUNNING).build();

		batchJobTrackingRepository.save(batchJobTrackInfoEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trackJobFinished(final String batchJobId, final boolean successful) {
		final BatchJobTrackInfoEntity batchJobTrackInfoEntity = batchJobTrackingRepository.getReferenceById(batchJobId);
		batchJobTrackInfoEntity.setFinishTime(TimeMachine.now());
		batchJobTrackInfoEntity.setStatus(successful ? BatchJobStatus.FINISHED : BatchJobStatus.FINISHED_WITH_FAILURES);

		batchJobTrackingRepository.save(batchJobTrackInfoEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trackJobFailure(final String batchJobId, final String batchJobType) {
		final Optional<BatchJobTrackInfoEntity> batchJobTrackInfoEntity = batchJobTrackingRepository
				.findById(batchJobId);
		batchJobTrackInfoEntity.ifPresentOrElse(this::markJobAsFailed, () -> createFailedJob(batchJobId, batchJobType));
	}

	private void markJobAsFailed(final BatchJobTrackInfoEntity batchJobTrackInfoEntity) {
		final BatchJobTrackInfoEntity batchJobTrackInfoUpdated = batchJobTrackInfoEntity.toBuilder()
				.status(BatchJobStatus.FAILED).finishTime(TimeMachine.now()).build();

		batchJobTrackingRepository.save(batchJobTrackInfoUpdated);
	}

	private void createFailedJob(final String batchJobId, final String batchJobType) {
		final BatchJobTrackInfoEntity batchJobTrackInfo = BatchJobTrackInfoEntity.builder().batchJobId(batchJobId)
				.batchJobType(batchJobType).status(BatchJobStatus.FAILED).startTime(TimeMachine.now())
				.finishTime(TimeMachine.now()).build();

		batchJobTrackingRepository.save(batchJobTrackInfo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markNonFinishedJobsAsAborted(final String batchJobType) {
		final List<BatchJobTrackInfoEntity> batchJobTrackInfoEntities = batchJobTrackingRepository
				.findByBatchJobTypeAndStatusIn(batchJobType, JOB_NOT_FINISHED_STATUSES);

		batchJobTrackInfoEntities.forEach(this::markJobAsAborted);
	}

	@Override
	public void markNonFinishedJobsAsAborted() {
		final List<BatchJobTrackInfoEntity> batchJobTrackInfoEntities = batchJobTrackingRepository
				.findByStatusIn(JOB_NOT_FINISHED_STATUSES);

		batchJobTrackInfoEntities.forEach(this::markJobAsAborted);
	}

	private void markJobAsAborted(final BatchJobTrackInfoEntity batchJobTrackInfoEntity) {
		batchJobTrackInfoEntity.setStatus(BatchJobStatus.ABORTED);

		batchJobTrackingRepository.save(batchJobTrackInfoEntity);

		//@formatter:off
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntitiesUpdated = batchJobItemTrackingRepository
				.findByBatchJobId(batchJobTrackInfoEntity.getBatchJobId()).stream()
					.filter(this::neededToMarkAsAbortedItems)
					.map(it -> it.toBuilder().status(BatchJobItemStatus.ABORTED).build())
					.collect(Collectors.toList());
		//@formatter:on

		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntitiesUpdated);
	}

	private boolean neededToMarkAsAbortedItems(final BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity) {
		return !ITEM_NOT_FINISHED_STATUSES.contains(batchJobItemTrackInfoEntity.getStatus());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends BatchJobItem<?>> void trackJobItemsAdded(final String batchJobId, final Collection<T> items) {
		final List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities = items.stream()
				.map(it -> createJobItemTracking(batchJobId, it)).collect(Collectors.toList());

		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities);
	}

	private <T extends BatchJobItem<?>> BatchJobItemTrackInfoEntity createJobItemTracking(final String batchJobId,
			final T item) {
		return BatchJobItemTrackInfoEntity.builder().batchJobId(batchJobId).itemId(item.getItemId())
				.itemType(item.getItemType()).status(BatchJobItemStatus.PENDING).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends BatchJobItem<?>> void trackJobItemProcessingStarted(final String batchJobId, final T item) {
		updatedJobItemStatus(batchJobId, item, BatchJobItemStatus.IN_PROGRESS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends BatchJobItem<?>> void trackJobItemProcessingFinished(final String batchJobId, final T item,
			final boolean successful) {
		updatedJobItemStatus(batchJobId, item, successful ? BatchJobItemStatus.SUCCESSFUL : BatchJobItemStatus.FAILED);
	}

	private <T extends BatchJobItem<?>> void updatedJobItemStatus(final String batchJobId, final T item,
			final BatchJobItemStatus status) {
		final BatchJobItemTrackingInfoId batchJobItemTrackingInfoId = BatchJobItemTrackingInfoId.builder()
				.batchJobId(batchJobId).itemType(item.getItemType()).itemId(item.getItemId()).build();

		final BatchJobItemTrackInfoEntity batchJobItemTrackingInfoUpdated = batchJobItemTrackingRepository
				.getReferenceById(batchJobItemTrackingInfoId).toBuilder().status(status).build();

		updateJobItemTimes(batchJobItemTrackingInfoUpdated);

		batchJobItemTrackingRepository.save(batchJobItemTrackingInfoUpdated);
	}

	private void updateJobItemTimes(final BatchJobItemTrackInfoEntity batchJobItemTrackingInfoUpdated) {

		if (BatchJobItemStatus.IN_PROGRESS.equals(batchJobItemTrackingInfoUpdated.getStatus())) {

			batchJobItemTrackingInfoUpdated.setStartTime(TimeMachine.now());
		}
		else if (ITEM_FINISHED_STATUSES.contains(batchJobItemTrackingInfoUpdated.getStatus())) {

			batchJobItemTrackingInfoUpdated.setFinishTime(TimeMachine.now());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BatchJobItemTrackInfoEntity> getItemsBeingProcessedOrEnquedToProcess(final String itemType) {
		return batchJobItemTrackingRepository.findByItemTypeAndStatusIn(itemType, ITEM_NOT_FINISHED_STATUSES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BatchJobTrackInfoEntity> getJobTrackingEntries(final LocalDateTime from, final LocalDateTime to) {
		return batchJobTrackingRepository.findByStartTimeIsBetween(from, to);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BatchJobItemTrackInfoEntity> getJobItemTrackingEntries(final String batchJobId) {
		return batchJobItemTrackingRepository.findByBatchJobId(batchJobId);
	}

	@Override
	public Optional<BatchJobTrackInfoEntity> findLastJobExecutionWithNonEmptyExtraction(final String batchJobType,
			final LocalDateTime from) {
		return batchJobTrackingRepository.findLastJobExecutionsWithItems(batchJobType, from, Pageable.ofSize(1))
				.stream().findFirst();
	}

}
