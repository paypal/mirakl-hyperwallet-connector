package com.paypal.testsupport;

import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletConnectionConfiguration;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
//@formatter:off
public class IntegrationTestConfig {

	@Value("${server.url}")
	private String serverUrl;

	private final MiraklApiClientConfig miraklApiClientConfig;

	private final HyperwalletConnectionConfiguration hyperwalletConnectionConfiguration;

	public IntegrationTestConfig(final MiraklApiClientConfig miraklApiClientConfig, final HyperwalletConnectionConfiguration hyperwalletConnectionConfiguration) {
		this.miraklApiClientConfig = miraklApiClientConfig;
		this.hyperwalletConnectionConfiguration = hyperwalletConnectionConfiguration;
	}

	@PostConstruct
	public void setupClients() {
		miraklApiClientConfig.setEnvironment(serverUrl);
		hyperwalletConnectionConfiguration.setServer(serverUrl);
	}

}
