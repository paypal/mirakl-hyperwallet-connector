package com.paypal.infrastructure.batchjob.repository;

import com.paypal.infrastructure.batchjob.BatchJobStatus;
import com.paypal.infrastructure.batchjob.entities.BatchJobTrackInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface BatchJobTrackingRepository extends JpaRepository<BatchJobTrackInfoEntity, String> {

	/**
	 * Retrieves a {@link List} of {@link BatchJobTrackInfoEntity} by the given batch job
	 * id and a {@link Set} of statuses.
	 * @param batchJobId the batch job id what to search for.
	 * @param statuses the {@link Set} of statuses what to search for.
	 * @return a {@link List} of {@link BatchJobTrackInfoEntity} by the given batch job id
	 * and a {@link Set} of statuses.
	 */
	List<BatchJobTrackInfoEntity> findByBatchJobTypeAndStatusIn(String batchJobId, Set<BatchJobStatus> statuses);

	/**
	 * Retrieves a {@link List} of {@link BatchJobTrackInfoEntity} by the given
	 * {@link Set} of statuses.
	 * @param statuses the {@link Set} of statuses what to search for.
	 * @return a {@link List} of {@link BatchJobTrackInfoEntity} by the given {@link Set}
	 * of statuses.
	 */
	List<BatchJobTrackInfoEntity> findByStatusIn(Set<BatchJobStatus> statuses);

	/**
	 * Retrieves a {@link List} of {@link BatchJobTrackInfoEntity} between the given
	 * {@link LocalDateTime}s from and to.
	 * @param from a {@link LocalDateTime} what to search for.
	 * @param to a {@link LocalDateTime} what to search for.
	 * @return a {@link List} of {@link BatchJobTrackInfoEntity} between the given
	 * {@link LocalDateTime}s from and to.
	 */
	List<BatchJobTrackInfoEntity> findByStartTimeIsBetween(LocalDateTime from, LocalDateTime to);

}
