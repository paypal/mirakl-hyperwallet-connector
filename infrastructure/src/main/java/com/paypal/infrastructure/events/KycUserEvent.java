package com.paypal.infrastructure.events;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Event for KyC user notifications
 */
public class KycUserEvent extends HMCEvent {

	public KycUserEvent(final Object source, final HyperwalletWebhookNotification notification) {
		super(source, notification);
	}

}
