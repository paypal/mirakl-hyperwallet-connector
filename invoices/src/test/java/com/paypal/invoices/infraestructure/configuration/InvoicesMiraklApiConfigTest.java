package com.paypal.invoices.infraestructure.configuration;

import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InvoicesMiraklApiConfigTest {

	private static final String API_KEY = "API_KEY";

	private static final String ENV = "ENV";

	@InjectMocks
	private InvoicesMiraklApiConfig testObj;

	@Test
	void invoicesMiraklMarketplacePlatformOperatorApiClient_shouldReturnWellFormedMiraklMarketplacePlatformOperatorApiClient() {
		testObj.setEnvironment(ENV);
		testObj.setOperatorApiKey(API_KEY);

		final MiraklMarketplacePlatformOperatorApiWrapper result = testObj
				.invoicesMiraklMarketplacePlatformOperatorApiClient();

		assertThat(result).hasFieldOrPropertyWithValue("defaultEndpoint.path", ENV)
				.hasFieldOrPropertyWithValue("defaultHttpContext.map", Map.of("Authorization", API_KEY));
	}

}
