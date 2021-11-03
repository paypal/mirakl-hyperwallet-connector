package com.paypal.infrastructure.events;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event base for HMC events
 */
@Getter
public abstract class HMCEvent extends ApplicationEvent {

	private final transient HyperwalletWebhookNotification notification;

	protected HMCEvent(final Object source, final HyperwalletWebhookNotification notification) {
		super(source);
		this.notification = notification;
	}

}
