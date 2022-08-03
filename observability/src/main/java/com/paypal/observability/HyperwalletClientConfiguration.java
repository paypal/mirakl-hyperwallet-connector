package com.paypal.observability;

import com.paypal.infrastructure.hyperwallet.api.DefaultHyperwalletApiConfig;
import com.paypal.infrastructure.hyperwallet.api.DefaultHyperwalletSDKService;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DefaultHyperwalletApiConfig.class)
public class HyperwalletClientConfiguration {

	@Bean
	HyperwalletSDKService observabilityHyperwalletSDKService(DefaultHyperwalletApiConfig defaultHyperwalletApiConfig) {
		return new DefaultHyperwalletSDKService(defaultHyperwalletApiConfig);
	}

}
