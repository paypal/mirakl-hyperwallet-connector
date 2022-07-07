package com.paypal.infrastructure.batchjob.repository;

import com.paypal.infrastructure.batchjob.BatchJobStatus;
import com.paypal.infrastructure.batchjob.entities.BatchJobItemTrackInfoEntity;
import com.paypal.infrastructure.batchjob.entities.BatchJobTrackInfoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	/**
	 * Returns all the {@link BatchJobTrackInfoEntity} which more than 0
	 * {@link BatchJobItemTrackInfoEntity} associated to its execution.
	 * @param batchJobType The batch job type.
	 * @param from The mimimun {@link LocalDateTime} of the jobs to find.
	 * @param pageable a {@link Pageable} to control de paging of the query.
	 * @return a {@link List} of {@link BatchJobTrackInfoEntity} with more than 0 items
	 * extracted
	 */
	@SuppressWarnings("java:S2479")
	@Query("""
			SELECT j FROM BatchJobTrackInfoEntity j
				LEFT JOIN BatchJobItemTrackInfoEntity ji ON j.batchJobId = ji.batchJobId
				WHERE j.batchJobType = :batchJobType
					AND j.startTime >= :from
				GROUP BY j.batchJobId, j.batchJobType, j.startTime, j.finishTime, j.status
				HAVING COUNT(ji.itemId) > 0
				ORDER BY j.startTime DESC
			""")
	List<BatchJobTrackInfoEntity> findLastJobExecutionsWithItems(@Param("batchJobType") String batchJobType,
			@Param("from") LocalDateTime from, Pageable pageable);

}
