package com.paypal.sellers.infrastructure.utils;

import com.mirakl.client.core.exception.MiraklException;

/**
 * Util class for logging errors of {@link MiraklException}
 */
public class MiraklLoggingErrorsUtil {

	private MiraklLoggingErrorsUtil() {
	}

	/**
	 * Stringifies the exception {@link MiraklException}
	 * @param exception the {@link MiraklException}
	 * @return the stringified version of {@link MiraklException}
	 */
	public static String stringify(final MiraklException exception) {
		final StringBuilder stringifyError = new StringBuilder();
		stringifyError.append("{");
		stringifyError.append("exceptionMessage=").append(exception.getMessage()).append(",");
		stringifyError.append("}");

		return stringifyError.toString();
	}

}
