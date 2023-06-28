package com.paypal.infrastructure.mirakl.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@EqualsAndHashCode
@Configuration
@ConfigurationProperties(prefix = "hmc.mirakl.connection")
public class MiraklApiClientConfig {

	private String operatorApiKey;

	private String environment;

}
