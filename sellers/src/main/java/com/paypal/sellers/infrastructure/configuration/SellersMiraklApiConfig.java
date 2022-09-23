package com.paypal.sellers.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

/**
 * Configuration class to instantiate Mirakl API client
 */
@Data
@ConfigurationProperties(prefix = "sellers.mirakl")
@PropertySource({ "classpath:sellers.properties" })
@Configuration
public class SellersMiraklApiConfig {

	private Long testingDelay;

	private String timeZone;

}
