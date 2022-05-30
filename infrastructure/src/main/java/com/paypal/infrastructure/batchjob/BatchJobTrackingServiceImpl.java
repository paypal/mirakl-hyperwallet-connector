package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackInfoEntity;
import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackingInfoId;
import com.paypal.infrastructure.batchjob.entities.BatchJobTrackInfoEntity;
import com.paypal.infrastructure.batchjob.repository.BatchJobItemTrackingRepository;
import com.paypal.infrastructure.batchjob.repository.BatchJobTrackingRepository;
import com.paypal.infrastructure.util.TimeMachine;
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

	public BatchJobTrackingServiceImpl(BatchJobTrackingRepository batchJobTrackingRepository,
			BatchJobItemTrackingRepository batchJobItemTrackingRepository) {
		this.batchJobTrackingRepository = batchJobTrackingRepository;
		this.batchJobItemTrackingRepository = batchJobItemTrackingRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trackJobStart(String batchJobId, String batchJobType) {
		BatchJobTrackInfoEntity batchJobTrackInfoEntity = BatchJobTrackInfoEntity.builder().batchJobId(batchJobId)
				.batchJobType(batchJobType).startTime(TimeMachine.now()).status(BatchJobStatus.RUNNING).build();

		batchJobTrackingRepository.save(batchJobTrackInfoEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trackJobFinished(String batchJobId, boolean successful) {
		BatchJobTrackInfoEntity batchJobTrackInfoEntity = batchJobTrackingRepository.getById(batchJobId);
		batchJobTrackInfoEntity.setFinishTime(TimeMachine.now());
		batchJobTrackInfoEntity.setStatus(successful ? BatchJobStatus.FINISHED : BatchJobStatus.FINISHED_WITH_FAILURES);

		batchJobTrackingRepository.save(batchJobTrackInfoEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void trackJobFailure(String batchJobId, String batchJobType) {
		Optional<BatchJobTrackInfoEntity> batchJobTrackInfoEntity = batchJobTrackingRepository.findById(batchJobId);
		batchJobTrackInfoEntity.ifPresentOrElse(this::markJobAsFailed, () -> createFailedJob(batchJobId, batchJobType));
	}

	private void markJobAsFailed(BatchJobTrackInfoEntity batchJobTrackInfoEntity) {
		BatchJobTrackInfoEntity batchJobTrackInfoUpdated = batchJobTrackInfoEntity.toBuilder()
				.status(BatchJobStatus.FAILED).finishTime(TimeMachine.now()).build();

		batchJobTrackingRepository.save(batchJobTrackInfoUpdated);
	}

	private void createFailedJob(String batchJobId, String batchJobType) {
		BatchJobTrackInfoEntity batchJobTrackInfo = BatchJobTrackInfoEntity.builder().batchJobId(batchJobId)
				.batchJobType(batchJobType).status(BatchJobStatus.FAILED).startTime(TimeMachine.now())
				.finishTime(TimeMachine.now()).build();

		batchJobTrackingRepository.save(batchJobTrackInfo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markNonFinishedJobsAsAborted(String batchJobType) {
		List<BatchJobTrackInfoEntity> batchJobTrackInfoEntities = batchJobTrackingRepository
				.findByBatchJobTypeAndStatusIn(batchJobType, JOB_NOT_FINISHED_STATUSES);

		batchJobTrackInfoEntities.forEach(this::markJobAsAborted);
	}

	@Override
	public void markNonFinishedJobsAsAborted() {
		List<BatchJobTrackInfoEntity> batchJobTrackInfoEntities = batchJobTrackingRepository
				.findByStatusIn(JOB_NOT_FINISHED_STATUSES);

		batchJobTrackInfoEntities.forEach(this::markJobAsAborted);
	}

	private void markJobAsAborted(final BatchJobTrackInfoEntity batchJobTrackInfoEntity) {
		batchJobTrackInfoEntity.setStatus(BatchJobStatus.ABORTED);

		batchJobTrackingRepository.save(batchJobTrackInfoEntity);

		//@formatter:off
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntitiesUpdated = batchJobItemTrackingRepository
				.findByBatchJobId(batchJobTrackInfoEntity.getBatchJobId()).stream()
					.filter(this::neededToMarkAsAbortedItems)
					.map(it -> it.toBuilder().status(BatchJobItemStatus.ABORTED).build())
					.collect(Collectors.toList());
		//@formatter:on

		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntitiesUpdated);
	}

	private boolean neededToMarkAsAbortedItems(BatchJobItemTrackInfoEntity batchJobItemTrackInfoEntity) {
		return !ITEM_NOT_FINISHED_STATUSES.contains(batchJobItemTrackInfoEntity.getStatus());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends BatchJobItem<?>> void trackJobItemsAdded(String batchJobId, Collection<T> items) {
		List<BatchJobItemTrackInfoEntity> batchJobItemTrackInfoEntities = items.stream()
				.map(it -> createJobItemTracking(batchJobId, it)).collect(Collectors.toList());

		batchJobItemTrackingRepository.saveAll(batchJobItemTrackInfoEntities);
	}

	private <T extends BatchJobItem<?>> BatchJobItemTrackInfoEntity createJobItemTracking(String batchJobId, T item) {
		return BatchJobItemTrackInfoEntity.builder().batchJobId(batchJobId).itemId(item.getItemId())
				.itemType(item.getItemType()).status(BatchJobItemStatus.PENDING).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends BatchJobItem<?>> void trackJobItemProcessingStarted(String batchJobId, T item) {
		updatedJobItemStatus(batchJobId, item, BatchJobItemStatus.IN_PROGRESS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends BatchJobItem<?>> void trackJobItemProcessingFinished(String batchJobId, T item,
			boolean successful) {
		updatedJobItemStatus(batchJobId, item, successful ? BatchJobItemStatus.SUCCESSFUL : BatchJobItemStatus.FAILED);
	}

	private <T extends BatchJobItem<?>> void updatedJobItemStatus(String batchJobId, T item,
			BatchJobItemStatus status) {
		BatchJobItemTrackingInfoId batchJobItemTrackingInfoId = BatchJobItemTrackingInfoId.builder()
				.batchJobId(batchJobId).itemType(item.getItemType()).itemId(item.getItemId()).build();

		BatchJobItemTrackInfoEntity batchJobItemTrackingInfoUpdated = batchJobItemTrackingRepository
				.getById(batchJobItemTrackingInfoId).toBuilder().status(status).build();

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
	public List<BatchJobItemTrackInfoEntity> getItemsBeingProcessedOrEnquedToProcess(String itemType) {
		return batchJobItemTrackingRepository.findByItemTypeAndStatusIn(itemType, ITEM_NOT_FINISHED_STATUSES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BatchJobTrackInfoEntity> getJobTrackingEntries(LocalDateTime from, LocalDateTime to) {
		return batchJobTrackingRepository.findByStartTimeIsBetween(from, to);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BatchJobItemTrackInfoEntity> getJobItemTrackingEntries(String batchJobId) {
		return batchJobItemTrackingRepository.findByBatchJobId(batchJobId);
	}

}
