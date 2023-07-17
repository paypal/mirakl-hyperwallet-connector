package com.paypal.observability.trafficauditor.adapters.support;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TrafficAuditorAdapterUtilsTest {

	@Test
	void cleanMap_shouldRemoveNullKeys() {
		// given
		final Map<String, String> map = new HashMap<>();
		map.put("key1", "value1");
		map.put(null, "value2");

		// when
		final Map<String, String> result = TrafficAuditorAdapterUtils.cleanMap(map);

		// then
		assertThat(result).containsOnlyKeys("key1").containsValues("value1");
	}

	@Test
	void cleanMap_shouldObfuscateAuthorizationEntry_whenValueIsString_ignoringCase() {
		// given
		final Map<String, String> map = new HashMap<>();
		map.put("AuthorIzatIon", "value1");
		map.put(null, "value2");

		// when
		final Map<String, String> result = TrafficAuditorAdapterUtils.cleanMap(map);

		// then
		assertThat(result).containsEntry("AuthorIzatIon", "********");
	}

	@Test
	void cleanMap_shouldObfuscateAuthorizationEntry_whenValueIsListOfString_ignoringCase() {
		// given
		final Map<String, List<String>> map = new HashMap<>();
		map.put("AuthorIzatIon", List.of("value1"));
		map.put(null, List.of("value2"));

		// when
		final Map<String, List<String>> result = TrafficAuditorAdapterUtils.cleanMap(map);

		// then
		assertThat(result.get("AuthorIzatIon").get(0)).isEqualTo("********");
	}

}
