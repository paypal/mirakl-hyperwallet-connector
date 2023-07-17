package com.paypal.jobsystem.batchjobfailures.services.retrypolicies;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.jobsystem.batchjobfailures.services.BatchJobFailedItemService;
import org.springframework.stereotype.Component;

@Component
public class AttemptsRetryPolicy implements BatchJobFailedItemRetryPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRetryFailedItem(final BatchJobFailedItem batchJobFailedItem) {

		return batchJobFailedItem.getNumberOfRetries() < BatchJobFailedItemService.MAX_ATTEMPTS;
	}

}
