package com.paypal.infrastructure.batchjob;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Batch job failed items repository.
 */
@Transactional
@Repository
public interface BatchJobFailedItemRepository extends JpaRepository<BatchJobFailedItem, BatchJobFailedItemId> {

}
