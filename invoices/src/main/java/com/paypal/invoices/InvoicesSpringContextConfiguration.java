package com.paypal.invoices;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import com.paypal.invoices.infraestructure.configuration.InvoicesHyperwalletApiConfig;
import com.paypal.invoices.infraestructure.configuration.InvoicesMiraklApiConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableConfigurationProperties({ InvoicesMiraklApiConfig.class, InvoicesHyperwalletApiConfig.class })
@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
public class InvoicesSpringContextConfiguration {

}
