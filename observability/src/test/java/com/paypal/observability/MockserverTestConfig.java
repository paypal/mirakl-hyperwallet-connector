package com.paypal.observability;

import com.paypal.infrastructure.hyperwallet.api.DefaultHyperwalletSDKUserService;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.hyperwallet.api.UserHyperwalletApiConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.impl.MiraklApiClientConfig;
import com.paypal.infrastructure.sdk.mirakl.impl.MiraklMarketplacePlatformOperatorApiClientWrapperImpl;

@Configuration
@Import({ UserHyperwalletApiConfig.class, MiraklApiClientConfig.class })
public class MockserverTestConfig {

	@Value("${server.url}")
	private String serverUrl;

	@Bean
	public MiraklMarketplacePlatformOperatorApiWrapper observabilityMiraklMarketplacePlatformOperatorApiClient(
			final MiraklApiClientConfig config) {
		config.setEnvironment(serverUrl);
		return new MiraklMarketplacePlatformOperatorApiClientWrapperImpl(config);
	}

	@Bean
	public HyperwalletSDKUserService observabilityHyperwalletSDKService(
			final UserHyperwalletApiConfig defaultHyperwalletApiConfig) {
		defaultHyperwalletApiConfig.setServer(serverUrl);
		return new DefaultHyperwalletSDKUserService(defaultHyperwalletApiConfig, null);
	}

}
