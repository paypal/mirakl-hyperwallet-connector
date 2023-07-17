package com.paypal.notifications.incoming.services.evaluators.predicates;

import com.paypal.notifications.storage.services.NotificationStorageService;

/**
 * Abstract notification predicate for handling common functionality.
 */
public abstract class AbstractNotificationPredicate {

	protected final NotificationStorageService notificationStorageService;

	protected AbstractNotificationPredicate(final NotificationStorageService notificationStorageService) {
		this.notificationStorageService = notificationStorageService;
	}

}
