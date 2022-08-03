package com.paypal.observability;

import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.infrastructure.hyperwallet.api.DefaultHyperwalletApiConfig;
import com.paypal.infrastructure.hyperwallet.api.DefaultHyperwalletSDKService;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DefaultHyperwalletApiConfig.class)
public class MockserverTestConfig {

	@Value("${server.url}")
	private String serverUrl;

	@Bean
	public MiraklMarketplacePlatformOperatorApiClient observabilityMiraklMarketplacePlatformOperatorApiClient() {
		return new MiraklMarketplacePlatformOperatorApiClient(serverUrl, new MiraklCredential("OPERATOR-KEY"));
	}

	@Bean
	public HyperwalletSDKService observabilityHyperwalletSDKService(
			DefaultHyperwalletApiConfig defaultHyperwalletApiConfig) {
		defaultHyperwalletApiConfig.setServer(serverUrl);
		return new DefaultHyperwalletSDKService(defaultHyperwalletApiConfig);
	}

}
