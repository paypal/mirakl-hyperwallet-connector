package com.paypal.reports;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import com.paypal.reports.infraestructure.configuration.ReportsMiraklApiConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@ComponentScan
@EnableConfigurationProperties({ ReportsMiraklApiConfig.class })
@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
@Profile("financial-report")
public class ReportsSpringContextConfiguration {

}
