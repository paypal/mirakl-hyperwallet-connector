package com.paypal.notifications.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.jobs.NotificationProcessJob;
import com.paypal.notifications.repository.NotificationsInMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test/hyperwallet-webhooknotifications")
@Profile({ "!prod" })
public class OverrideHyperwalletWebhookNotificationsMockController {

	@Autowired
	private NotificationsInMemoryStore notificationsInMemoryStore;

	/**
	 * Stores in memory a list of Hyperwallet notifications. When
	 * {@link NotificationProcessJob} tries to retrieve the information of a notification
	 * by its token, if a notification with that token was stored, then it's used as the
	 * notification to be processed.
	 *
	 * If a notification with that token was not previously stored the
	 * {@link NotificationProcessJob} connects to Hyperwallet services to return the
	 * information of the failed notification.
	 * @param notifications a list of {@link HyperwalletWebhookNotification} that will be
	 * used instead of retrieving them the Hyperwallet services
	 */
	@PutMapping("/")
	public void storeNotifications(@RequestBody(required = false) List<HyperwalletWebhookNotification> notifications) {

		log.debug("Adding the following hyperwallet webhook notifications to execution context: %s"
				.formatted(notifications.stream().map(HyperwalletWebhookNotification::getToken)
						.collect(Collectors.joining(", "))));
		notificationsInMemoryStore.clear();
		notificationsInMemoryStore.addNotifications(notifications);
	}

}
