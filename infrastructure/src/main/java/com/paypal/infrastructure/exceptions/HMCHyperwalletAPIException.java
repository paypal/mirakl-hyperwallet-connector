package com.paypal.infrastructure.exceptions;

import com.hyperwallet.clientsdk.HyperwalletException;

/**
 * Base exception for all Hyperwallet Connector exceptions.
 */
public class HMCHyperwalletAPIException extends HMCException {

	protected static final String DEFAULT_MSG = "An error has occurred while invoking Hyperwallet API";

	private final HyperwalletException hyperwalletException;

	public HMCHyperwalletAPIException(final String message, final HyperwalletException e) {
		super(message, e);
		hyperwalletException = e;
	}

	public HMCHyperwalletAPIException(HyperwalletException e) {
		super(DEFAULT_MSG, e);
		hyperwalletException = e;
	}

	public HyperwalletException getHyperwalletException() {
		return hyperwalletException;
	}

}
