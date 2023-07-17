package com.paypal.notifications.events.services.eventpublishers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.events.model.HMCEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Abstract class that defines the publication of events
 */
@Slf4j
public abstract class AbstractHMCEventSender {

	@Resource
	private ApplicationEventPublisher applicationEventPublisher;

	public Void execute(final HyperwalletWebhookNotification notification) {
		log.info("Notification sent to [{}]", notification.getType());

		applicationEventPublisher.publishEvent(getEvent(notification));

		return null;
	}

	protected abstract HMCEvent getEvent(final HyperwalletWebhookNotification notification);

}
