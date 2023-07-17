package com.paypal.infrastructure.support.exceptions;

import com.mirakl.client.core.exception.MiraklException;

/**
 * Base exception for all Mirakl Connector exceptions.
 */
public class HMCMiraklAPIException extends HMCException {

	protected static final String DEFAULT_MSG = "An error has occurred while invoking Mirakl API";

	private final MiraklException miraklException;

	public HMCMiraklAPIException(final String message, final MiraklException e) {
		super(message, e);
		miraklException = e;
	}

	public HMCMiraklAPIException(final MiraklException e) {
		super(DEFAULT_MSG, e);
		miraklException = e;
	}

	public MiraklException getMiraklException() {
		return miraklException;
	}

}
