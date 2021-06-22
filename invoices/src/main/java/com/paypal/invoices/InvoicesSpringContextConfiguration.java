package com.paypal.invoices;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import com.paypal.invoices.infraestructure.configuration.InvoicesHyperwalletApiConfig;
import com.paypal.invoices.infraestructure.configuration.InvoicesMiraklApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ InvoicesMiraklApiConfig.class, InvoicesHyperwalletApiConfig.class })
@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
public class InvoicesSpringContextConfiguration {

	public static void main(final String[] args) {
		SpringApplication.run(InvoicesSpringContextConfiguration.class, args);
	}

}
