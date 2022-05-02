package com.paypal.notifications;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan
@PropertySource({ "classpath:notifications.properties" })
@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
public class NotificationsSpringContextConfiguration {

}
