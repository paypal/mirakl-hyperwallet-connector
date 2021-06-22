package com.paypal.kyc.infrastructure.configuration;

import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

/**
 * Configuration class to instantiate Mirakl API client
 */
@Data
@ConfigurationProperties(prefix = "kyc.mirakl")
@PropertySource({ "classpath:kyc.properties" })
@Configuration
public class KYCMiraklApiConfig {

	private String operatorApiKey;

	private String environment;

	/**
	 * Creates a bean to handle api calls with a
	 * {@link MiraklMarketplacePlatformOperatorApiClient}
	 * @return the {@link MiraklMarketplacePlatformOperatorApiClient}
	 */
	@Bean
	@PostConstruct
	public MiraklMarketplacePlatformOperatorApiClient kycMiraklMarketplacePlatformOperatorApiClient() {
		return new MiraklMarketplacePlatformOperatorApiClient(environment, new MiraklCredential(operatorApiKey));
	}

}
