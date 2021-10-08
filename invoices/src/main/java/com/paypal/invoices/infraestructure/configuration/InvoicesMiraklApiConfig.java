package com.paypal.invoices.infraestructure.configuration;

import com.mirakl.client.core.security.MiraklCredential;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.impl.MiraklMarketplacePlatformOperatorApiClientWrapperImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * Configuration class to instantiate Mirakl API client
 */
@Data
@ConfigurationProperties(prefix = "invoices.mirakl")
@PropertySource({ "classpath:invoices.properties" })
@Configuration
public class InvoicesMiraklApiConfig {

	private String operatorApiKey;

	private String environment;

	/**
	 * Creates a bean to handle api calls with a
	 * {@link MiraklMarketplacePlatformOperatorApiWrapper}
	 * @return the {@link MiraklMarketplacePlatformOperatorApiWrapper}
	 */
	@Bean
	@PostConstruct
	public MiraklMarketplacePlatformOperatorApiWrapper invoicesMiraklMarketplacePlatformOperatorApiClient() {
		return new MiraklMarketplacePlatformOperatorApiClientWrapperImpl(environment,
				new MiraklCredential(operatorApiKey));
	}

	/**
	 * Creates {@link RestTemplate} bean to handle rest calls for BDD testing
	 * @return the {@link RestTemplate}
	 */
	@Bean
	@Profile({ "qa" })
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
