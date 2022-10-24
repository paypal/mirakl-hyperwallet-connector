package com.paypal.invoices.infraestructure.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Set;

/**
 * Configuration class to handle the payment notifications
 */
@Getter
@PropertySource({ "classpath:invoices.properties" })
@Configuration
public class PaymentNotificationConfig {

	@Value("#{'${payments.notifications.failureStatuses}'}")
	private Set<String> failureStatuses;

	@Value("#{'${payments.notifications.acceptedStatuses}'}")
	private Set<String> acceptedStatuses;

}
