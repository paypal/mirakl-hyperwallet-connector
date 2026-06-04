package com.paypal.notifications.incoming.cache;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;

/**
 * Read-through cache for {@link HyperwalletWebhookNotification} objects keyed by webhook
 * token. Callers should use this service instead of calling the Hyperwallet SDK directly
 * during notification processing to avoid hitting rate limits.
 */
public interface WebhookNotificationRetriever {

	/**
	 * Returns the {@link HyperwalletWebhookNotification} for the given token. If the
	 * entry is present in the cache it is returned immediately; otherwise the SDK is
	 * called and the result is stored in the cache.
	 * @param programToken the Hyperwallet program token associated with the notification.
	 * @param webhookToken the webhook notification token.
	 * @return the {@link HyperwalletWebhookNotification}, or {@code null} if the SDK call
	 * fails.
	 */
	HyperwalletWebhookNotification get(String programToken, String webhookToken);

	/**
	 * Stores the given {@link HyperwalletWebhookNotification} in the cache under its
	 * token. Intended to be called at webhook reception time so subsequent processing job
	 * runs can skip the SDK call.
	 * @param notification the notification to cache.
	 */
	void put(HyperwalletWebhookNotification notification);

}
