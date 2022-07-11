package com.paypal.observability;

import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockserverTestConfig {

	@Value("${server.url}")
	private String serverUrl;

	@Bean
	public MiraklMarketplacePlatformOperatorApiClient observabilityMiraklMarketplacePlatformOperatorApiClient() {
		return new MiraklMarketplacePlatformOperatorApiClient(serverUrl, new MiraklCredential("OPERATOR-KEY"));
	}

}
