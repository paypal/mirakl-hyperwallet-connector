package com.paypal.reports;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import com.paypal.reports.infraestructure.configuration.ReportsMiraklApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableConfigurationProperties({ ReportsMiraklApiConfig.class })
@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
@Profile("financial-report")
public class ReportsSpringContextConfiguration {

	public static void main(final String[] args) {
		SpringApplication.run(ReportsSpringContextConfiguration.class, args);
	}

}
