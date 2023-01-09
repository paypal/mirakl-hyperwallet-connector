package com.paypal.infrastructure.sdk.mirakl.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@EqualsAndHashCode
@Configuration
@ConfigurationProperties(prefix = "infrastructure.mirakl")
public class MiraklApiClientConfig {

	private String operatorApiKey;

	private String environment;

}
