package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackInfoEntity;
import com.paypal.infrastructure.batchjob.entities.BatchJobTrackInfoEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
	void trackJobItemsAdded(String batchJobId, Collection<BatchJobItem<?>> items);

	/**
	 * Track job item processing as started.
	 * @param batchJobId the batch job id,
	 * @param item the {@link BatchJobItem} item.
	 */
	void trackJobItemProcessingStarted(String batchJobId, BatchJobItem<?> item);

	/**
	 * Track job item processing as finished.
	 * @param batchJobId the btach job id,
	 * @param item the {@link BatchJobItem} item.
	 * @param successful if successful.
	 */
	void trackJobItemProcessingFinished(String batchJobId, BatchJobItem<?> item, boolean successful);

	/**
	 * Retrieves a {@link List} of {@link BatchJobItemTrackInfoEntity} that are being
	 * processed or are going to be processed.
	 * @param itemType the item type.
	 * @returna {@link List} of {@link BatchJobItemTrackInfoEntity} that are being
	 * processed or are going to be processed.
	 */
	List<BatchJobItemTrackInfoEntity> getItemsBeingProcessedOrEnquedToProcess(String itemType);

	/**
	 * Retrieves a {@link List} of {@link BatchJobTrackInfoEntity} between the given
	 * {@link LocalDateTime} from and to.
	 * @param from a {@link LocalDateTime}.
	 * @param to a {@link LocalDateTime}.
	 * @returna {@link List} of {@link BatchJobTrackInfoEntity} between the given
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

}
