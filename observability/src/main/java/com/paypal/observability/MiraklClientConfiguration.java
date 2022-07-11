package com.paypal.observability;

import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({ "classpath:observability.properties" })
public class MiraklClientConfiguration {

	@Value("${mirakl.operatorApiKey}")
	private String operatorApiKey;

	@Value("${mirakl.environment}")
	private String environment;

	@Bean
	public MiraklMarketplacePlatformOperatorApiClient observabilityMiraklMarketplacePlatformOperatorApiClient() {
		return new MiraklMarketplacePlatformOperatorApiClient(environment, new MiraklCredential(operatorApiKey));
	}

}
