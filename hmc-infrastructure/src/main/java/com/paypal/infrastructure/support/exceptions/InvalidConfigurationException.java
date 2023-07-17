package com.paypal.infrastructure.support.exceptions;

public class InvalidConfigurationException extends HMCException {

	public InvalidConfigurationException(final String message) {
		super(message);
	}

	public InvalidConfigurationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
