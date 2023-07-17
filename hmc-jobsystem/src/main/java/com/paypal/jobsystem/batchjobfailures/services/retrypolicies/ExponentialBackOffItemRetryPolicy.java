package com.paypal.jobsystem.batchjobfailures.services.retrypolicies;

import com.paypal.jobsystem.batchjobfailures.repositories.entities.BatchJobFailedItem;
import com.paypal.infrastructure.support.date.TimeMachine;
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
	public boolean shouldRetryFailedItem(final BatchJobFailedItem batchJobFailedItem) {
		final LocalDateTime currentTime = TimeMachine.now();
		final LocalDateTime nextRetryTime = nextRetryTime(batchJobFailedItem);

		return currentTime.isEqual(nextRetryTime) || currentTime.isAfter(nextRetryTime(batchJobFailedItem));
	}

	private LocalDateTime nextRetryTime(final BatchJobFailedItem batchJobFailedItem) {
		final LocalDateTime referenceTime = batchJobFailedItem.getLastRetryTimestamp() == null
				? batchJobFailedItem.getFirstFailureTimestamp() : batchJobFailedItem.getLastRetryTimestamp();
		return referenceTime.plusMinutes(minutesPerRetry * batchJobFailedItem.getNumberOfRetries());
	}

	public void setMinutesPerRetry(final long minutesPerRetry) {
		this.minutesPerRetry = minutesPerRetry;
	}

}
