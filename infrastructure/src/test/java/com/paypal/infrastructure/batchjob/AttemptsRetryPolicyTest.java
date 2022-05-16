package com.paypal.infrastructure.batchjob;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttemptsRetryPolicyTest {

	@InjectMocks
	private AttemptsRetryPolicy testObj;

	@Mock
	private BatchJobFailedItem batchJobFailedItemMock;

	@Test
	void shouldRetryFailedItem_ShouldReturnTrue_WhenNumberOfRetriesIsLessThanFive() {

		when(batchJobFailedItemMock.getNumberOfRetries()).thenReturn(4);

		final boolean result = testObj.shouldRetryFailedItem(batchJobFailedItemMock);

		assertThat(result).isTrue();
	}

	@Test
	void shouldRetryFailedItem_ShouldReturnFalse_WhenNumberOfRetriesIsBiggerOrEqualThanFive() {

		when(batchJobFailedItemMock.getNumberOfRetries()).thenReturn(5);

		final boolean result = testObj.shouldRetryFailedItem(batchJobFailedItemMock);

		assertThat(result).isFalse();
	}

}
