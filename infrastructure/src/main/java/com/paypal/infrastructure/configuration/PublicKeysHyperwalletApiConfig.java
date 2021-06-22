package com.paypal.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "hyperwallet.api")
@PropertySource({ "classpath:application.properties" })
public class PublicKeysHyperwalletApiConfig {

	protected String hmcPublicKeyLocation;

}
