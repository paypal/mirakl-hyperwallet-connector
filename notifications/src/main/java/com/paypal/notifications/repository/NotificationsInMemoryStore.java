package com.paypal.notifications.repository;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Stores Hyperwallet Webhooks Notifications in memory allowing simple operations such as
 * retrieving a notification by its token.
 */
@Component
@Profile("!prod")
public class NotificationsInMemoryStore {

	private Map<String, HyperwalletWebhookNotification> notifications;

	public NotificationsInMemoryStore() {
		this.notifications = new HashMap<>();
	}

	/**
	 * Retrieves a previously stored notification by its token.
	 * @param token The token of the notification to retrieve
	 * @return The notification that matches the provided token or empty if it's not found
	 * in the store.
	 */
	public Optional<HyperwalletWebhookNotification> getNotificationByToken(final String token) {
		return Optional.ofNullable(notifications.get(token));
	}

	/**
	 * Add a notification to the store.
	 * @param notification The notification to be stored
	 */
	public void addNotification(final HyperwalletWebhookNotification notification) {
		notifications.put(notification.getToken(), notification);
	}

	/**
	 * Adds a collection of notifications to the store.
	 * @param notifications The collection of notifications to be stored
	 */
	public void addNotifications(final Collection<HyperwalletWebhookNotification> notifications) {
		notifications.stream().forEach(this::addNotification);
	}

	/**
	 * Removes all previously stored elements
	 */
	public void clear() {
		notifications.clear();
	}

}
