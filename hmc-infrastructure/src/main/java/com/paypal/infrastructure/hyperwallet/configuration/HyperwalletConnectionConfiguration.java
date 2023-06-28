package com.paypal.infrastructure.hyperwallet.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("hmc.hyperwallet.connection")
public class HyperwalletConnectionConfiguration {

	protected String server;

	protected String username;

	protected String password;

}
