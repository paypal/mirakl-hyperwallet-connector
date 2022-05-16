package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.util.TimeMachine;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExponentialBackOffItemRetryPolicy implements BatchJobFailedItemRetryPolicy {

	private static final int MINUTES_PER_RETRY = 30;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRetryFailedItem(BatchJobFailedItem batchJobFailedItem) {
		return TimeMachine.now().isAfter(nextRetryTime(batchJobFailedItem));
	}

	private LocalDateTime nextRetryTime(BatchJobFailedItem batchJobFailedItem) {
		LocalDateTime referenceTime = batchJobFailedItem.getLastRetryTimestamp() == null
				? batchJobFailedItem.getFirstFailureTimestamp() : batchJobFailedItem.getLastRetryTimestamp();
		return referenceTime.plusMinutes((long) MINUTES_PER_RETRY * batchJobFailedItem.getNumberOfRetries());
	}

}
