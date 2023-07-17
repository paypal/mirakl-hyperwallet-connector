package com.paypal.jobsystem.batchjobfailures.services.retrypolicies;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;

public interface BatchJobFailedItemRetryPolicy {

	/**
	 * Checks if a {@link BatchJobFailedItem} is retryable.
	 * @param batchJobFailedItem the {@link BatchJobFailedItem}.
	 * @return true if the {@link BatchJobFailedItem} is retryable, false otherwise.
	 */
	boolean shouldRetryFailedItem(BatchJobFailedItem batchJobFailedItem);

}
