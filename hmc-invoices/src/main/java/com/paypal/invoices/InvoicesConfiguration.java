package com.paypal.invoices;

import com.paypal.infrastructure.InfrastructureConfiguration;
import com.paypal.jobsystem.JobSystemConfiguration;
import com.paypal.notifications.NotificationsConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan
@Import({ InfrastructureConfiguration.class, JobSystemConfiguration.class, NotificationsConfiguration.class })
public class InvoicesConfiguration {

}
