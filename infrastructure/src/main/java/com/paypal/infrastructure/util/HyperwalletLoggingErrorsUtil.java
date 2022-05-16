package com.paypal.infrastructure.util;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletError;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.CollectionUtils;

/**
 * Util class for logging errors of {@link HyperwalletException}
 */
public final class HyperwalletLoggingErrorsUtil {

	private HyperwalletLoggingErrorsUtil() {
	}

	/**
	 * Stringifies the exception {@link HyperwalletException}
	 * @param exception the {@link HyperwalletException}
	 * @return the stringified version of {@link HyperwalletException}
	 */
	public static String stringify(final HyperwalletException exception) {
		final StringBuilder stringifyError = new StringBuilder();
		stringifyError.append("{");
		stringifyError.append("exceptionMessage=").append(exception.getMessage()).append(",");
		stringifyError.append("error=").append(exception.getErrorCode());
		getErrorList(exception, stringifyError);
		stringifyError.append("}");

		return stringifyError.toString();
	}

	protected static void getErrorList(final HyperwalletException exception, final StringBuilder stringifyError) {
		if (!CollectionUtils.isEmpty(exception.getHyperwalletErrors())) {
			stringifyError.append("[");
			for (final HyperwalletError hyperwalletError : exception.getHyperwalletErrors()) {
				stringifyError.append(
						ToStringBuilder.reflectionToString(hyperwalletError, ToStringStyle.NO_CLASS_NAME_STYLE));
			}
			stringifyError.append("]");
		}
	}

}
