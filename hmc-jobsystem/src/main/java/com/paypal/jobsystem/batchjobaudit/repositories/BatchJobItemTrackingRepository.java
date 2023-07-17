package com.paypal.jobsystem.batchjobaudit.repositories;

import com.paypal.jobsystem.batchjob.model.BatchJobItemStatus;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobItemTrackInfoEntity;
import com.paypal.jobsystem.batchjobaudit.repositories.entities.BatchJobItemTrackingInfoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BatchJobItemTrackingRepository
		extends JpaRepository<BatchJobItemTrackInfoEntity, BatchJobItemTrackingInfoId> {

	/**
	 * Retrieves a {@link List} of {@link BatchJobItemTrackInfoEntity} by the given batch
	 * job id.
	 * @param batchJobId the batch job id what to search for.
	 * @returna {@link List} of {@link BatchJobItemTrackInfoEntity} by the given batch job
	 * id.
	 */
	List<BatchJobItemTrackInfoEntity> findByBatchJobId(String batchJobId);

	/**
	 * Retrieves a {@link List} of {@link BatchJobItemTrackInfoEntity} by the given batch
	 * job type and a {@link Set} of statuses.
	 * @param batchJobType the bath job type what to search for.
	 * @param statuses the {@link Set} of statuses what to search for.
	 * @return a {@link List} of {@link BatchJobItemTrackInfoEntity} by the given batch
	 * job type and a {@link Set} of statuses.
	 */
	List<BatchJobItemTrackInfoEntity> findByItemTypeAndStatusIn(String batchJobType, Set<BatchJobItemStatus> statuses);

}
