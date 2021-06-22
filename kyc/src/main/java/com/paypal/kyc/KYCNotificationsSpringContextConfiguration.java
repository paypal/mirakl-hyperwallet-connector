package com.paypal.kyc;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import com.paypal.kyc.infrastructure.configuration.KYCMiraklApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
@EnableConfigurationProperties({ KYCMiraklApiConfig.class })
@SpringBootApplication
public class KYCNotificationsSpringContextConfiguration {

	public static void main(final String[] args) {
		SpringApplication.run(KYCNotificationsSpringContextConfiguration.class, args);
	}

}
