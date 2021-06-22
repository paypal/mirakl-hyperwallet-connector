package com.paypal.reports.infraestructure.configuration;

import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReportsMiraklApiConfigTest {

	private static final String API_KEY = "API_KEY";

	private static final String ENV = "ENV";

	@InjectMocks
	private ReportsMiraklApiConfig testObj;

	@Test
	void reportsMiraklMarketplacePlatformOperatorApiClient_shouldReturnWellFormedMiraklMarketplacePlatformOperatorApiClient() {
		testObj.setEnvironment(ENV);
		testObj.setOperatorApiKey(API_KEY);

		final MiraklMarketplacePlatformOperatorApiClient result = testObj
				.reportsMiraklMarketplacePlatformOperatorApiClient();

		assertThat(result).hasFieldOrPropertyWithValue("defaultEndpoint.path", ENV)
				.hasFieldOrPropertyWithValue("defaultHttpContext.map", Map.of("Authorization", API_KEY));
	}

}
