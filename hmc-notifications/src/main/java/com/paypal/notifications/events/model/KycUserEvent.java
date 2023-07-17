package com.paypal.notifications.events.model;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Event for KyC user notifications
 */
public class KycUserEvent extends HMCEvent {

	public KycUserEvent(final Object source, final HyperwalletWebhookNotification notification) {
		super(source, notification);
	}

}
