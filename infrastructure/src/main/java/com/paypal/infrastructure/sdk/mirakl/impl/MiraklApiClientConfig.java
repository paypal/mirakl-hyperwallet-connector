package com.paypal.infrastructure.sdk.mirakl.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Configuration
@ConfigurationProperties(prefix = "infrastructure.mirakl")
@PropertySource({ "classpath:infrastructure.properties" })
public class MiraklApiClientConfig {

	private String operatorApiKey;

	private String environment;

}
