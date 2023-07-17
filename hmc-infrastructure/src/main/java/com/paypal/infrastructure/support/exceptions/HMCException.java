package com.paypal.infrastructure.support.exceptions;

/**
 * Base exception for all internal errors in Hyperwallet-Mirakl Connector.
 */
public class HMCException extends RuntimeException {

	public HMCException() {
	}

	public HMCException(final String message) {
		super(message);
	}

	public HMCException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public HMCException(final Throwable cause) {
		super(cause);
	}

	public HMCException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
