package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.util.TimeMachine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ExponentialBackOffItemRetryPolicy implements BatchJobFailedItemRetryPolicy {

	public static final long MINUTES_PER_RETRY = 30;

	private long minutesPerRetry = MINUTES_PER_RETRY;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRetryFailedItem(BatchJobFailedItem batchJobFailedItem) {
		LocalDateTime currentTime = TimeMachine.now();
		LocalDateTime nextRetryTime = nextRetryTime(batchJobFailedItem);

		return currentTime.isEqual(nextRetryTime) || currentTime.isAfter(nextRetryTime(batchJobFailedItem));
	}

	private LocalDateTime nextRetryTime(BatchJobFailedItem batchJobFailedItem) {
		LocalDateTime referenceTime = batchJobFailedItem.getLastRetryTimestamp() == null
				? batchJobFailedItem.getFirstFailureTimestamp() : batchJobFailedItem.getLastRetryTimestamp();
		return referenceTime.plusMinutes(minutesPerRetry * batchJobFailedItem.getNumberOfRetries());
	}

	public void setMinutesPerRetry(long minutesPerRetry) {
		this.minutesPerRetry = minutesPerRetry;
	}

}
