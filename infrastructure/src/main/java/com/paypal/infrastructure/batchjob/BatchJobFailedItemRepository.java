package com.paypal.infrastructure.batchjob;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Batch job failed items repository.
 */
@Repository
public interface BatchJobFailedItemRepository extends JpaRepository<BatchJobFailedItem, BatchJobFailedItemId> {

	/**
	 * Retrieves a {@link List} of {@link BatchJobFailedItem} by the given type and
	 * {@link BatchJobFailedItemStatus} status.
	 * @param type the {@link BatchJobFailedItem} type.
	 * @param status the {@link BatchJobFailedItem} status.
	 * @return a {@link List} of {@link BatchJobFailedItem} by the given type and
	 * {@link BatchJobFailedItemStatus} status.
	 */
	List<BatchJobFailedItem> findByTypeAndStatus(String type, BatchJobFailedItemStatus status);

	/**
	 * Retrieves a {@link List} of {@link BatchJobFailedItem} by the given type.
	 * @param type the {@link BatchJobFailedItem} type.
	 * @return a {@link List} of {@link BatchJobFailedItem} by the given type.
	 */
	List<BatchJobFailedItem> findByType(String type);

}
