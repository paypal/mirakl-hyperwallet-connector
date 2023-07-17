package com.paypal.notifications.events.model;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Event for payment notifications
 */
public class PaymentEvent extends HMCEvent {

	public PaymentEvent(final Object source, final HyperwalletWebhookNotification notification) {
		super(source, notification);
	}

}
