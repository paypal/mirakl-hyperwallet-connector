package com.paypal.sellers.infrastructure.configuration;

import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

/**
 * Configuration class to instantiate Mirakl API client
 */
@Data
@ConfigurationProperties(prefix = "sellers.mirakl")
@PropertySource({ "classpath:sellers.properties" })
@Configuration
public class SellersMiraklApiConfig {

	private String operatorApiKey;

	private String environment;

	private Long testingDelay;

	private String timeZone;

	/**
	 * Creates a bean to handle api calls with a
	 * {@link MiraklMarketplacePlatformOperatorApiClient}
	 * @return the {@link MiraklMarketplacePlatformOperatorApiClient}
	 */
	@Primary
	@Bean
	@PostConstruct
	public MiraklMarketplacePlatformOperatorApiClient sellersMiraklMarketplacePlatformOperatorApiClient() {
		return new MiraklMarketplacePlatformOperatorApiClient(environment, new MiraklCredential(operatorApiKey));
	}

}
