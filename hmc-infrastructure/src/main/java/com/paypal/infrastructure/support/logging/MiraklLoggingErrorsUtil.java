package com.paypal.infrastructure.support.logging;

import com.mirakl.client.core.exception.MiraklException;

/**
 * Util class for logging errors of {@link MiraklException}
 */
public final class MiraklLoggingErrorsUtil {

	private static final String MESSAGE_FORMAT = "{exceptionMessage=%s,}";

	private MiraklLoggingErrorsUtil() {
	}

	/**
	 * Stringifies the exception {@link MiraklException}
	 * @param exception the {@link MiraklException}
	 * @return the stringified version of {@link MiraklException}
	 */
	public static String stringify(final MiraklException exception) {
		return MESSAGE_FORMAT.formatted(exception.getMessage());
	}

}
