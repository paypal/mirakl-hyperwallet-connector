package com.paypal.kyc;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import com.paypal.kyc.infrastructure.configuration.KYCMiraklApiConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
@EnableConfigurationProperties({ KYCMiraklApiConfig.class })
@ComponentScan
public class KYCNotificationsSpringContextConfiguration {

}
