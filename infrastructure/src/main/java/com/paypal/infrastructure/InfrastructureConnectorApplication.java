package com.paypal.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@ComponentScan
@PropertySource({ "classpath:infrastructure.properties" })
public class InfrastructureConnectorApplication {

}
