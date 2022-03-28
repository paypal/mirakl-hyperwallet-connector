package com.paypal.infrastructure.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Wraps HMC API error messages.
 */
@Getter
@RequiredArgsConstructor
public class HMCErrorResponse {

	/**
	 * The error message used in the API response.
	 */
	private final String errorMessage;

}
