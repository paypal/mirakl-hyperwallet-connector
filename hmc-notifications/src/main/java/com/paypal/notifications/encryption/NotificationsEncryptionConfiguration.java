package com.paypal.notifications.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.notifications.encryption.httpconverters.JWEConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class NotificationsEncryptionConfiguration {

	@ConditionalOnProperty("hmc.hyperwallet.encryption.encryptionEnabled")
	@Bean
	public JWEConverter jweConverter(final HyperwalletEncryption hyperwalletEncryption,
			final ObjectMapper objectMapper) {
		return new JWEConverter(hyperwalletEncryption, objectMapper);
	}

}
