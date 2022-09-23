package com.paypal.invoices.infraestructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class to instantiate Mirakl API client
 */
@Data
@ConfigurationProperties(prefix = "invoices.mirakl")
@PropertySource({ "classpath:invoices.properties" })
@Configuration
public class InvoicesMiraklApiConfig {

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
