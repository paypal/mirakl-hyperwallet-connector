package com.paypal.infrastructure.events;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Event for payment notifications
 */
public class PaymentEvent extends HMCEvent {

	public PaymentEvent(final Object source, final HyperwalletWebhookNotification notification) {
		super(source, notification);
	}

}
