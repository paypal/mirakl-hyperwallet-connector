package com.paypal.infrastructure.test;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import com.paypal.infrastructure.hyperwallet.api.PaymentsHyperwalletApiConfig;
import com.paypal.infrastructure.hyperwallet.api.UserHyperwalletApiConfig;
import com.paypal.infrastructure.sdk.mirakl.impl.MiraklApiClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@Configuration
//@formatter:off
@Import({InfrastructureConnectorApplication.class})
public class InfrastructureTestConfig {

	@Value("${server.url}")
	private String serverUrl;
	private final MiraklApiClientConfig miraklApiClientConfig;
	private final PaymentsHyperwalletApiConfig paymentsHyperwalletApiConfig;
	private final UserHyperwalletApiConfig userHyperwalletApiConfig;

	public InfrastructureTestConfig(final MiraklApiClientConfig miraklApiClientConfig,
									final PaymentsHyperwalletApiConfig paymentsHyperwalletApiConfig,
									final UserHyperwalletApiConfig userHyperwalletApiConfig) {
		this.miraklApiClientConfig = miraklApiClientConfig;
		this.paymentsHyperwalletApiConfig = paymentsHyperwalletApiConfig;
		this.userHyperwalletApiConfig = userHyperwalletApiConfig;
	}

	@PostConstruct
	public void setupClients() {
		miraklApiClientConfig.setEnvironment(serverUrl);
		paymentsHyperwalletApiConfig.setServer(serverUrl);
		userHyperwalletApiConfig.setServer(serverUrl);
	}
}
