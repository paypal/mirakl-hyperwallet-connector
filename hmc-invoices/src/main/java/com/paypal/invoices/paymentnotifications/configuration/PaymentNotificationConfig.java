package com.paypal.invoices.paymentnotifications.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Configuration class to handle the payment notifications
 */
@Getter
@Configuration
public class PaymentNotificationConfig {

	@Value("#{'${hmc.webhooks.payments.failure-statuses}'}")
	private Set<String> failureStatuses;

	@Value("#{'${hmc.webhooks.payments.accepted-statuses}'}")
	private Set<String> acceptedStatuses;

	@Value("#{'${hmc.webhooks.payments.confirm-linked-documents}'}")
	private boolean confirmLinkedDocuments;

}
