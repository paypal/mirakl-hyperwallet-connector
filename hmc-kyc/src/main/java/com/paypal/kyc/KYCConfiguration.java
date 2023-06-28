package com.paypal.kyc;

import com.paypal.infrastructure.InfrastructureConfiguration;
import com.paypal.notifications.NotificationsConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({ InfrastructureConfiguration.class, NotificationsConfiguration.class })
@ComponentScan
public class KYCConfiguration {

}
