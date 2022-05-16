package com.paypal.infrastructure.batchjob;

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
