package com.paypal.invoices.paymentnotifications.model;

import com.paypal.notifications.storage.model.NotificationBodyModel;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents an payment notification
 */
@Getter
@Builder(toBuilder = true)
public class PaymentNotificationBodyModel implements NotificationBodyModel {

	private final String token;

	private final String status;

	private final String createdOn;

	private final String amount;

	private final String currency;

	private final String clientPaymentId;

	private final String notes;

	private final String purpose;

	private final String releaseOn;

	private final String expiresOn;

	private final String destinationToken;

	private final String programToken;

}
