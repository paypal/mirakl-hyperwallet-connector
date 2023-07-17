package com.paypal.observability.trafficauditor.adapters.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParamExtractorTest {

	@Test
	void getQueryParametersFromUri_WhenUriHasNoQueryParams_ShouldReturnEmptyMap() {
		// given
		final String uri = "http://localhost:8080/health";

		// when
		final Map<String, String> result = QueryParamExtractor.getQueryParametersFromUri(uri);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void getQueryParametersFromUri_WhenUriHasQueryParams_ShouldReturnMapOfQueryParams() {
		// given
		final String uri = "http://localhost:8080/health?param1=value1&param2=value2";

		// when
		final Map<String, String> result = QueryParamExtractor.getQueryParametersFromUri(uri);

		// then
		assertThat(result).containsOnlyKeys("param1", "param2").containsValues("value1", "value2");
	}

}
