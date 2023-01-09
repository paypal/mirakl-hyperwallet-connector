package com.paypal.invoices;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import com.paypal.invoices.infraestructure.configuration.InvoicesMiraklApiConfig;
import com.paypal.infrastructure.hyperwallet.api.PaymentsHyperwalletApiConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableConfigurationProperties({ InvoicesMiraklApiConfig.class, PaymentsHyperwalletApiConfig.class })
@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
public class InvoicesSpringContextConfiguration {

}
