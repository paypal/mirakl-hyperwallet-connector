package com.paypal.infrastructure.batchjob;

import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExponentialBackOffItemRetryPolicyTest {

	@InjectMocks
	private ExponentialBackOffItemRetryPolicy testObj;

	@Test
	void shouldRetryFailedItem_ShouldReturnTrue_WhenJobIsRetryable() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		final BatchJobFailedItem batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setLastRetryTimestamp(now.minusHours(1));
		batchJobFailedItem.setNumberOfRetries(0);

		final boolean result = testObj.shouldRetryFailedItem(batchJobFailedItem);

		assertThat(result).isTrue();
	}

	@Test
	void shouldRetryFailedItem_ShouldReturnTrue_WhenJobIsRetryableAndLastRetryTimestampIsNull() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		final BatchJobFailedItem batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setFirstFailureTimestamp(now.minusHours(1));
		batchJobFailedItem.setLastRetryTimestamp(null);
		batchJobFailedItem.setNumberOfRetries(0);

		final boolean result = testObj.shouldRetryFailedItem(batchJobFailedItem);

		assertThat(result).isTrue();
	}

	@Test
	void shouldRetryFailedItem_ShouldReturnFalse_WhenJobIsNotRetryable() {

		final LocalDateTime now = TimeMachine.now();
		TimeMachine.useFixedClockAt(now);

		final BatchJobFailedItem batchJobFailedItem = new BatchJobFailedItem();
		batchJobFailedItem.setLastRetryTimestamp(now);
		batchJobFailedItem.setNumberOfRetries(1);

		final boolean result = testObj.shouldRetryFailedItem(batchJobFailedItem);

		assertThat(result).isFalse();
	}

}
