package com.paypal.observability.trafficauditor.adapters.support;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class QueryParamExtractor {

	private QueryParamExtractor() {
		// Utility class
	}

	public static Map<String, String> getQueryParametersFromUri(@NotNull final String uri) {
		return uri.split("\\?").length > 1 ? parseQueryString(uri.split("\\?")[1]) : Map.of();
	}

	private static Map<String, String> parseQueryString(@NotNull final String queryString) {
		return Arrays.stream(queryString.split("&")).map(param -> param.split("="))
				.collect(Collectors.toMap(param -> param[0], param -> param[1]));
	}

}
