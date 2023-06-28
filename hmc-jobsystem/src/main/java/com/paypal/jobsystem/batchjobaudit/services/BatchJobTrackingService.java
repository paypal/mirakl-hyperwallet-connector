package com.paypal.jobsystem.batchjobaudit.services;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobItemTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobTrackInfoEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BatchJobTrackingService {

	/**
	 * Track job start with the given batch job id and batch job type.
	 * @param batchJobId the batch job id.
	 * @param batchJobType the batch job type.
	 */
	void trackJobStart(String batchJobId, String batchJobType);

	/**
	 * Track job finished with the given batch job id and if successful.
	 * @param batchJobId the batch job id.
	 * @param successful if successful.
	 */
	void trackJobFinished(String batchJobId, boolean successful);

	/**
	 * Track job failure with the given batch job id and batch job type.
	 * @param batchJobId the batch job id.
	 * @param batchJobType the batch job type.
	 */
	void trackJobFailure(String batchJobId, String batchJobType);

	/**
	 * Mark not finished jobs as aborted by the given batch job type.
	 * @param batchJobType the batch job type.
	 */
	void markNonFinishedJobsAsAborted(String batchJobType);

	/**
	 * Mark not finished jobs as aborted.
	 */
	void markNonFinishedJobsAsAborted();

	/**
	 * Track job items added to the job.
	 * @param batchJobId the batch job id
	 * @param items a {@link Collection} of {@link BatchJobItem}.
	 */
	<T extends BatchJobItem<?>> void trackJobItemsAdded(String batchJobId, Collection<T> items);

	/**
	 * Track job item processing as started.
	 * @param batchJobId the batch job id,
	 * @param item the {@link BatchJobItem} item.
	 */
	<T extends BatchJobItem<?>> void trackJobItemProcessingStarted(String batchJobId, T item);

	/**
	 * Track job item processing as finished.
	 * @param batchJobId the btach job id,
	 * @param item the {@link BatchJobItem} item.
	 * @param successful if successful.
	 */
	<T extends BatchJobItem<?>> void trackJobItemProcessingFinished(String batchJobId, T item, boolean successful);

	/**
	 * Retrieves a {@link List} of {@link BatchJobItemTrackInfoEntity} that are being
	 * processed or are going to be processed.
	 * @param itemType the item type.
	 * @return {@link List} of {@link BatchJobItemTrackInfoEntity} that are being
	 * processed or are going to be processed.
	 */
	List<BatchJobItemTrackInfoEntity> getItemsBeingProcessedOrEnquedToProcess(String itemType);

	/**
	 * Retrieves a {@link List} of {@link BatchJobTrackInfoEntity} between the given
	 * {@link LocalDateTime} from and to.
	 * @param from a {@link LocalDateTime}.
	 * @param to a {@link LocalDateTime}.
	 * @return {@link List} of {@link BatchJobTrackInfoEntity} between the given
	 * {@link LocalDateTime} from and to.
	 */
	List<BatchJobTrackInfoEntity> getJobTrackingEntries(LocalDateTime from, LocalDateTime to);

	/**
	 * Retrieves a {@link List} of {@link BatchJobItemTrackInfoEntity} by the given batch
	 * job id.
	 * @param batchJobId the batch job id.
	 * @return a {@link List} of {@link BatchJobItemTrackInfoEntity} by the given batch
	 * job id.
	 */
	List<BatchJobItemTrackInfoEntity> getJobItemTrackingEntries(String batchJobId);

	/**
	 * Retrieve the last job execution with extracted items.
	 * @param batchJobType the batch job type
	 * @param from a minimum starting date for the job
	 * @return a {@link List} of {@link BatchJobTrackInfoEntity} with more than 0 items
	 * extracted
	 */
	Optional<BatchJobTrackInfoEntity> findLastJobExecutionWithNonEmptyExtraction(String batchJobType,
			LocalDateTime from);

}
