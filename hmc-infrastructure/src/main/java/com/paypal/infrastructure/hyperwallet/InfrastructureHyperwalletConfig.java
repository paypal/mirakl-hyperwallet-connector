package com.paypal.infrastructure.hyperwallet;

import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletEncryptionConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class InfrastructureHyperwalletConfig {

	@ConditionalOnProperty("hmc.hyperwallet.encryption.encryptionEnabled")
	@Bean
	public HyperwalletEncryption hyperwalletEncryptionWrapper(
			final HyperwalletEncryptionConfiguration encryptionConfiguration) {
		//@formatter:off
		return new HyperwalletEncryption(
				JWEAlgorithm.parse(encryptionConfiguration.getEncryptionAlgorithm()),
				JWSAlgorithm.parse(encryptionConfiguration.getSignAlgorithm()),
				EncryptionMethod.parse(encryptionConfiguration.getEncryptionMethod()),
				encryptionConfiguration.getHmcKeySetLocation(),
				encryptionConfiguration.getHwKeySetLocation(),
				encryptionConfiguration.getExpirationMinutes());
		//@formatter:on
	}

}
