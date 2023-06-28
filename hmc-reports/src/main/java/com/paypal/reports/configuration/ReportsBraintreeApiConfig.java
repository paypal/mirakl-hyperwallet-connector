package com.paypal.reports.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration class to instantiate Braintree sdk client
 */
@Data
@ConfigurationProperties(prefix = "hmc.braintree.connection")
@Component
public class ReportsBraintreeApiConfig {

	private String merchantId;

	private String privateKey;

	private String publicKey;

	private String environment;

}
