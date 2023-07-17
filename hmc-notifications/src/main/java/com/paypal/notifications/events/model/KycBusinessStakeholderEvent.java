package com.paypal.notifications.events.model;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Event for KyC business stakeholder notifications
 */
public class KycBusinessStakeholderEvent extends HMCEvent {

	public KycBusinessStakeholderEvent(final Object source, final HyperwalletWebhookNotification notification) {
		super(source, notification);
	}

}
