package com.paypal.infrastructure.batchjob;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

/**
 * This class holds the result of a batch job item validation.
 */
@Value
@Builder
public class BatchJobItemValidationResult {

	private BatchJobItemValidationStatus status;

	@Builder.Default
	private Optional<String> reason = Optional.empty();

}
