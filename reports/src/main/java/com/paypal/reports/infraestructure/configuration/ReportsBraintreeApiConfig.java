package com.paypal.reports.infraestructure.configuration;

import com.braintreegateway.BraintreeGateway;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

/**
 * Configuration class to instantiate Braintree sdk client
 */
@Data
@ConfigurationProperties(prefix = "reports.braintree")
@PropertySource({ "classpath:reports.properties" })
@Configuration
public class ReportsBraintreeApiConfig {

	private String merchantId;

	private String privateKey;

	private String publicKey;

	private String environment;

	@Bean
	@PostConstruct
	BraintreeGateway getBraintreeSDKClient() {
		return new BraintreeGateway(environment, merchantId, publicKey, privateKey);
	}

}
