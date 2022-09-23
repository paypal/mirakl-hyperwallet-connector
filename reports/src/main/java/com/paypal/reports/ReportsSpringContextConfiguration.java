package com.paypal.reports;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@ComponentScan
@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
@Profile("financial-report")
public class ReportsSpringContextConfiguration {

}
