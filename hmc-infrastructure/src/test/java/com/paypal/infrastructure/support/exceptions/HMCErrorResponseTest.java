package com.paypal.infrastructure.support.exceptions;

import com.paypal.infrastructure.support.exceptions.HMCErrorResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HMCErrorResponseTest {

	private static final String ERROR_MESSAGE = "Error message";

	private static final String MESSAGE_ATTRIBUTE = "errorMessage";

	@Test
	void constructor_ShouldPopulateFromAndToDate_WhenCalled() {

		final HMCErrorResponse result = new HMCErrorResponse(ERROR_MESSAGE);

		assertThat(result).hasFieldOrPropertyWithValue(MESSAGE_ATTRIBUTE, ERROR_MESSAGE);
	}

	@Test
	void getFrom_ShouldReturnFromValue_WhenCalled() {

		final HMCErrorResponse result = new HMCErrorResponse(ERROR_MESSAGE);

		assertThat(result.getErrorMessage()).isEqualTo(ERROR_MESSAGE);
	}

}
