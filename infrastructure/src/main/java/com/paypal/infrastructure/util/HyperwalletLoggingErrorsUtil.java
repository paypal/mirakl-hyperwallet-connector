package com.paypal.infrastructure.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperwallet.clientsdk.HyperwalletException;
import lombok.extern.slf4j.Slf4j;

/**
 * Util class for logging errors of {@link HyperwalletException}
 */
@Slf4j
public final class HyperwalletLoggingErrorsUtil {

	private HyperwalletLoggingErrorsUtil() {
	}

	/**
	 * Stringifies the exception {@link HyperwalletException}
	 * @param exception the {@link HyperwalletException}
	 * @return the stringified version of {@link HyperwalletException}
	 */
	public static String stringify(final HyperwalletException exception) {
		HyperwalletErrorLogEntry logEntry = HyperwalletErrorLogEntryConverter.INSTANCE.from(exception);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logEntry);
		}
		catch (JsonProcessingException e) {
			log.warn("An error has been occurred while generating detailed error information from Hyperwallet error",
					e);
			return exception.getMessage();
		}
	}

}
