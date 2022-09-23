package com.paypal.kyc;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.paypal.infrastructure.InfrastructureConnectorApplication;

@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
@ComponentScan
public class KYCNotificationsSpringContextConfiguration {

}
