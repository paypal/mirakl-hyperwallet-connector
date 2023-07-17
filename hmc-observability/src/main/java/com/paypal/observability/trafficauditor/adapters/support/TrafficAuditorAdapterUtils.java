package com.paypal.observability.trafficauditor.adapters.support;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TrafficAuditorAdapterUtils {

	private TrafficAuditorAdapterUtils() {
		// private constructor
	}

	public static <T> Map<String, T> cleanMap(final Map<String, T> map) {
		Map<String, T> resultMap = map != null ? map : Map.of();
		resultMap = removeNullKeys(resultMap);
		resultMap = obfuscateAuthorization(resultMap);

		return resultMap;
	}

	private static <T> Map<String, T> removeNullKeys(@NotNull final Map<String, T> map) {
		return map.entrySet().stream().filter(entry -> entry.getKey() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private static <T> Map<String, T> obfuscateAuthorization(@NotNull final Map<String, T> map) {
		return map.entrySet().stream().map(TrafficAuditorAdapterUtils::obfuscateAuthorizationEntry)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private static <T> Map.Entry<String, T> obfuscateAuthorizationEntry(final Map.Entry<String, T> entry) {
		if (entry.getKey().equalsIgnoreCase("Authorization")) {
			return Map.entry(entry.getKey(), (T) obfuscateAuthorization(entry.getValue()));
		}
		return entry;
	}

	private static Object obfuscateAuthorization(final Object value) {
		if (value == null) {
			return null;
		}
		else if (value instanceof String) {
			return "********";
		}
		else if (value instanceof List) {
			return List.of("********");
		}

		return null;
	}

}
