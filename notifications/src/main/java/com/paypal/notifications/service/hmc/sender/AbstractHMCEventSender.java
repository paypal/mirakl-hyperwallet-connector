package com.paypal.notifications.service.hmc.sender;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.events.HMCEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;

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

	abstract HMCEvent getEvent(final HyperwalletWebhookNotification notification);

}
