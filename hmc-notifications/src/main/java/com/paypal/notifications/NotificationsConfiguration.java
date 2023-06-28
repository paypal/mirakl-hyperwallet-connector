package com.paypal.notifications;

import com.paypal.infrastructure.InfrastructureConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan
@Import({ InfrastructureConfiguration.class })
public class NotificationsConfiguration {

}
